package app.interpreter

import app.action.AppAction.Script
import app.action._
import app.infrastructure.FrameworkResponse
import cats.implicits._
import cats.~>

import scala.concurrent.{ExecutionContext, Future}

class DispatchInterpreter(eventStoreInterpreter: EventStoreInterpreter,
                          idGenerator: IdGeneratorInterpreter,
                          timeGenerator: TimeInterpreter,
                          configInterpreter: ConfigInterpreter,
                          loggingInterpreter: LoggingInterpreter)(implicit ec: ExecutionContext)
    extends AppInterpreter {

  val interpret: AppAction ~> Future = new (AppAction ~> Future) {
    override def apply[A](fa: AppAction[A]): Future[A] = fa match {
      case a: SaveEvent         => eventStoreInterpreter.run(a)
      case a: ListEvents        => eventStoreInterpreter.run(a)
      case a: ListEventsByRange => eventStoreInterpreter.run(a)
      case a: SaveSnapshot      => eventStoreInterpreter.run(a)
      case a: GetEventsCount    => eventStoreInterpreter.run(a)
      case a: GetLatestSnapshot => eventStoreInterpreter.run(a)
      case GenerateId           => Future.successful(idGenerator())
      case CurrentTime          => Future.successful(timeGenerator())
      case GetConfig            => Future.successful(configInterpreter())
      case LogAction(log)       => Future { loggingInterpreter.log(log) }
    }
  }

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse] =
    appAction.foldMap(interpret)

}
