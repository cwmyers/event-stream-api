package app.interpreter

import app.action._
import app.model.{Snapshot, Event}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TestEventStoreInterpreter(implicit ec: ExecutionContext) extends EventStoreInterpreter {
  val mutableEventMap = mutable.Map[String, Event]()
  val mutableSnapshotMap = mutable.Map[String, Snapshot]()

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event, next) => mutableEventMap += (event.id.id -> event); Future(next)
    case ListEvents(onResult) => Future(onResult(mutableEventMap.toList.map(_._2)))
    case ListEventsForEntity(entityId, from, to, onResult) =>
      Future {
        val eventsForEntity = mutableEventMap.filter {
          case (eventId, event) => event.entityId == entityId &&
            from.fold(true)(f => event.suppliedTimestamp.isAfter(f)) &&
            to.fold(true)(t => event.suppliedTimestamp.isBefore(t))
        }

        val orderedEvents: List[Event] = eventsForEntity.toList.map(_._2).sortWith {
          case (e1, e2) => e1.suppliedTimestamp.isBefore(e2.suppliedTimestamp)
        }
        onResult(orderedEvents)
      }
    case SaveSnapshot(snapshot, next) => mutableSnapshotMap += (snapshot.id.id -> snapshot); Future(next)
  }
}
