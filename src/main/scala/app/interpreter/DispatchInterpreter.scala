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
      case a:SaveEvent[A] => eventStoreInterpreter.run(a)
      case a:ListEvents[A] => eventStoreInterpreter.run(a)
      case a:ListEventsByRange[A] => eventStoreInterpreter.run(a)
      case a:SaveSnapshot[A] => eventStoreInterpreter.run(a)
      case a:GetEventsCount[A] => eventStoreInterpreter.run(a)
      case a:GetLatestSnapshot[A] => eventStoreInterpreter.run(a)
      case GenerateId(onResult) => Future(onResult(idGenerator()))
      case CurrentTime(onResult) => Future(onResult(timeGenerator()))
      case GetConfig(onResult) => Future(onResult(configInterpreter()))
      case LogAction(log, next) => Future{loggingInterpreter.log(log); next}
    }
  }

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse] =
    Free.runFC[AppAction, Future, FrameworkResponse](appAction)(interpret)

  implicit val futureMonadInstance = new Monad[Future] {
    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f

    override def point[A](a: => A): Future[A] = Future(a)
  }
}
