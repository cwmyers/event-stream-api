package interpreter

import app.action.{EventStoreAction, SaveEvent}
import app.model.Event

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TestEventStoreInterpreter(implicit ec:ExecutionContext) extends EventStoreInterpreter {
  val mutableMap = mutable.Map[String, Event]()
  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event, next) => mutableMap += ("1" -> event); Future(next)
  }
}
