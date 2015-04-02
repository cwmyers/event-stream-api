package interpreter

import app.action.AppAction.Script
import app.action.{SaveEvent, AppAction, EventStoreAction}
import app.interpreter.AppInterpreter
import infrastructure.FrameworkResponse

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Monad, ~>}

class DispatchInterpreter(eventStoreInterpreter: EventStoreInterpreter)(implicit ec: ExecutionContext) extends AppInterpreter {

  val exe: AppAction ~> Future = new (AppAction ~> Future) {
    override def apply[A](fa: AppAction[A]): Future[A] = fa match {
      case a:SaveEvent[A] => eventStoreInterpreter.run(a)
    }
  }

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse] = appAction.foldMap(exe)

  implicit val futureMonadInstance = new Monad[Future] {
    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f

    override def point[A](a: => A): Future[A] = Future(a)
  }
}
