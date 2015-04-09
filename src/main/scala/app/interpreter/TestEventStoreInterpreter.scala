package app.interpreter

import app.action.{EventStoreAction, ListEvents, ListEventsForEntity, SaveEvent}
import app.model.Event

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TestEventStoreInterpreter(implicit ec: ExecutionContext) extends EventStoreInterpreter {
  val mutableMap = mutable.Map[String, Event]()

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event, next) => mutableMap += (event.id.id -> event); Future(next)
    case ListEvents(onResult) => Future(onResult(mutableMap.toList.map(_._2)))
    case ListEventsForEntity(entityId, from, to, onResult) =>
      Future {
        val eventsForEntity = mutableMap.filter {
          case (eventId, event) => event.entityId == entityId &&
            from.fold(true)(f => event.suppliedTimestamp.isAfter(f)) &&
            to.fold(true)(t => event.suppliedTimestamp.isBefore(t))
        }

        val orderedEvents: List[Event] = eventsForEntity.toList.map(_._2).sortWith {
          case (e1, e2) => e1.suppliedTimestamp.isBefore(e2.suppliedTimestamp)
        }
        onResult(orderedEvents)
      }
  }
}
