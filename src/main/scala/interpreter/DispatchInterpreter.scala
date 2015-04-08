package interpreter

import app.action.AppAction.Script
import app.action._
import app.interpreter.AppInterpreter
import infrastructure.FrameworkResponse

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Monad, ~>}

class DispatchInterpreter(eventStoreInterpreter: EventStoreInterpreter,
                           idGenerator: IdGeneratorInterpreter,
                           timeGenerator: TimeInterpreter)
                         (implicit ec: ExecutionContext) extends AppInterpreter {

  val interpret: AppAction ~> Future = new (AppAction ~> Future) {
    override def apply[A](fa: AppAction[A]): Future[A] = fa match {
      case a:SaveEvent[A] => eventStoreInterpreter.run(a)
      case a:ListEvents[A] => eventStoreInterpreter.run(a)
      case a:ListEventsForEntity[A] => eventStoreInterpreter.run(a)
      case GenerateId(onResult) => Future(onResult(idGenerator()))
      case CurrentTime(onResult) => Future(onResult(timeGenerator()))
    }
  }

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse] = appAction foldMap interpret

  implicit val futureMonadInstance = new Monad[Future] {
    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f

    override def point[A](a: => A): Future[A] = Future(a)
  }
}
