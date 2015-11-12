package app.interpreter

import app.action.AppAction.Script
import app.action._
import app.infrastructure.FrameworkResponse

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Free, Monad, ~>}

class DispatchInterpreter(eventStoreInterpreter: EventStoreInterpreter,
                           idGenerator: IdGeneratorInterpreter,
                           timeGenerator: TimeInterpreter,
                           configInterpreter: ConfigInterpreter,
                           loggingInterpreter: LoggingInterpreter)
                         (implicit ec: ExecutionContext) extends AppInterpreter {

  val interpret: AppAction ~> Future = new (AppAction ~> Future) {
    override def apply[A](fa: AppAction[A]): Future[A] = fa match {
      case a:SaveEvent => eventStoreInterpreter.run(a)
      case a:ListEvents => eventStoreInterpreter.run(a)
      case a:ListEventsByRange => eventStoreInterpreter.run(a)
      case a:SaveSnapshot => eventStoreInterpreter.run(a)
      case a:GetEventsCount => eventStoreInterpreter.run(a)
      case a:GetLatestSnapshot => eventStoreInterpreter.run(a)
      case GenerateId => Future(idGenerator())
      case CurrentTime => Future(timeGenerator())
      case GetConfig => Future(configInterpreter())
      case LogAction(log) => Future{loggingInterpreter.log(log)}
    }
  }

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse] =
    Free.runFC[AppAction, Future, FrameworkResponse](appAction)(interpret)

  implicit val futureMonadInstance = new Monad[Future] {
    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f

    override def point[A](a: => A): Future[A] = Future(a)
  }
}
