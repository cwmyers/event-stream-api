package app.interpreter

import java.time.OffsetDateTime

import app.MaybeTime
import app.action._
import app.model._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class MutableMapEventStoreInterpreter(implicit ec: ExecutionContext)
    extends EventStoreInterpreter {
  val mutableEventMap    = mutable.Map[String, Event]()
  val mutableSnapshotMap = mutable.Map[String, Snapshot]()

  implicit def eventOrderInstance: Ordering[OffsetDateTime] = new Ordering[OffsetDateTime] {
    override def compare(x: OffsetDateTime, y: OffsetDateTime): Int = x compareTo y
  }

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = Future {
    eventStoreAction match {
      case SaveEvent(event) => mutableEventMap += (event.id -> event); ()
      case ListEvents(entityId, systemName, fromTime, toTime, pageSize, pageNumber) =>
        listEvents(entityId, systemName, fromTime, toTime, pageSize, pageNumber)
      case ListEventsByRange(entityId, systemName, from, to) =>
        listEventsByRange(entityId, systemName, from, to)
      case SaveSnapshot(snapshot) =>
        mutableSnapshotMap += (snapshot.id -> snapshot)
        ()
      case GetLatestSnapshot(entityId, systemName, time) =>
        getLatestSnapshot(entityId, systemName, time)
      case GetEventsCount(entityId, systemName, fromTime, toTime) =>
        getEventCount(entityId, systemName, fromTime, toTime)

    }
  }

  def getEventCount(entityId: Option[EntityId],
                    systemName: Option[SystemName],
                    fromTime: MaybeTime,
                    toTime: MaybeTime) = {
    val events = entityId.fold(mutableEventMap)(
      id => mutableEventMap.filter { case (key, event) => event.id == id }
    )
    val filtered = systemName.fold(events)(
      name => events.filter { case (key, event) => event.systemName == name }
    )
    filtered.size
  }

  def listEvents(entityId: Option[EntityId],
                 systemName: Option[SystemName],
                 fromTime: MaybeTime,
                 toTime: MaybeTime,
                 pageSize: Int,
                 pageNumber: Option[Long]): List[Event] = {
    val all: List[Event]            = mutableEventMap.toList.map(_._2)
    val entityFiltered: List[Event] = entityId.fold(all)(id => all.filter(_.entityId == id))
    val systemNameFiltered =
      systemName.fold(entityFiltered)(name => entityFiltered.filter(_.systemName == name))
    val sorted: List[Event] = systemNameFiltered.sortBy(_.suppliedTimestamp)
    pageNumber.fold(sorted.takeRight(pageSize)) { p =>
      val startIndex = p.toInt * pageSize
      val endIndex   = startIndex + pageSize
      sorted.slice(startIndex, endIndex)
    }

  }

  def listEventsByRange(entityId: EntityId,
                        systemName: SystemName,
                        from: Option[OffsetDateTime],
                        to: OffsetDateTime) = {
    val eventsForEntity = mutableEventMap.filter {
      case (eventId, event) =>
        event.entityId == entityId &&
          from.fold(true)(f => event.suppliedTimestamp.isAfter(f)) &&
          event.suppliedTimestamp.isBefore(to) &&
          event.systemName == systemName
    }
    eventsForEntity.toList.map(_._2).sortBy(_.suppliedTimestamp)
  }

  def getLatestSnapshot(entityId: EntityId, systemName: SystemName, time: OffsetDateTime) = {

    mutableSnapshotMap.filter {
      case (id, snapshot) =>
        snapshot.entityId == entityId &&
          snapshot.systemName == systemName
    }.toList.map(_._2).sortBy(_.timestamp).reverse.find(_.timestamp.isBefore(time))
  }

}
