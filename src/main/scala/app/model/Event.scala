package app.model

import java.time.OffsetDateTime

import io.circe.Json


case class ReceivedEvent(entityId: EntityId, systemName: SystemName, timestamp: OffsetDateTime, body: Json)

case class Event(id: EventId, entityId: EntityId,
                 systemName: SystemName,
                 createdTimestamp: OffsetDateTime,
                 suppliedTimestamp: OffsetDateTime, body: Json)

case class State(systemName: SystemName, body: Json)
case class Entity(id: EntityId, state: Set[State])

case class Snapshot(id: SnapshotId, entityId: EntityId, systemName: SystemName,
                    timestamp: OffsetDateTime, body: Json)


case class Links(selfPageUrl: URI, firstPageUrl: URI,
                 nextPageUrl: Option[URI], previousPageUrl: Option[URI])

case class LinkedResponse(events: List[Event], pageNumber: Option[Long], pageSize: Int, links: Links)


object Event {
  def fromReceivedEvent(receivedEvent: ReceivedEvent)(eventId: EventId, time: OffsetDateTime): Event =
    Event(eventId, receivedEvent.entityId, receivedEvent.systemName, time, receivedEvent.timestamp, receivedEvent.body)

  def replayEvents(events: List[Event]) = replayJsonEvents(events.map(_.body))

  def replayEventsWithSnapshot(snapshot: Option[Snapshot], events: List[Event]): Json =
    replayJsonEvents(snapshot.fold(Json.Null)(_.body) :: events.map(_.body))

  def replayJsonEvents(events:List[Json]): Json =
    events.foldLeft(Json.Null)(_ deepMerge _)

}
