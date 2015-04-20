package app.interpreter

import java.time.OffsetDateTime

import app.action._
import app.model.{Snapshot, Event}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TestEventStoreInterpreter(implicit ec: ExecutionContext) extends EventStoreInterpreter {
  val mutableEventMap = mutable.Map[String, Event]()
  val mutableSnapshotMap = mutable.Map[String, Snapshot]()

  implicit def eventOrderInstance: Ordering[OffsetDateTime] = new Ordering[OffsetDateTime] {
    override def compare(x: OffsetDateTime, y: OffsetDateTime): Int = x compareTo y
  }

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = Future {
    eventStoreAction match {
      case SaveEvent(event, next) => mutableEventMap += (event.id.id -> event); next

      case ListEvents(entityId, pageSize, pageNumber, onResult) => onResult {
        val all: List[Event] = mutableEventMap.toList.map(_._2)
        val filtered: List[Event] = entityId.fold(all)(id => all.filter(_.entityId == id))
        val sorted: List[Event] = filtered.sortBy(_.suppliedTimestamp)
        pageNumber.fold(sorted.takeRight(pageSize)) { p =>
          val startIndex = p.toInt * pageSize
          val endIndex = startIndex + pageSize
          sorted.slice(startIndex, endIndex)
        }
      }
      case ListEventsByRange(entityId, from, to, onResult) =>
        val eventsForEntity = mutableEventMap.filter {
          case (eventId, event) => event.entityId == entityId &&
            from.fold(true)(f => event.suppliedTimestamp.isAfter(f)) &&
            event.suppliedTimestamp.isBefore(to)
        }
        val orderedEvents: List[Event] = eventsForEntity.toList.map(_._2).sortBy(_.suppliedTimestamp)
        onResult(orderedEvents)

      case SaveSnapshot(snapshot, next) =>
        mutableSnapshotMap += (snapshot.id.id -> snapshot)
        next

      case GetLatestSnapshot(entityId, time, onResult) =>
        onResult(mutableSnapshotMap.filter { case (id, snapshot) => snapshot.entityId.id == entityId.id}.toList.map(_._2).sortBy(_.timestamp).find(_.timestamp.isBefore(time)))

      case GetEventsCount(entityId, onResult) => onResult {
        val events = entityId.fold(mutableEventMap)(id => mutableEventMap.filter { case (key, entity) => entity.id.id == id.id})
        events.size
      }
    }
  }
}
