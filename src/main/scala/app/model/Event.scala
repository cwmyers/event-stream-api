package app.model

import java.time.OffsetDateTime

import argonaut.Json


case class EventId(id: String) extends AnyVal
case class EntityId(id: String) extends AnyVal
case class SystemName(name: String) extends AnyVal
case class SnapshotId(id: String) extends AnyVal
case class URI(url:String) extends AnyVal


case class ReceivedEvent(entityId: EntityId, systemName: SystemName, timestamp: OffsetDateTime, body: Json)

case class Event(id: EventId, entityId: EntityId,
                 systemName: SystemName,
                 createdTimestamp: OffsetDateTime,
                 suppliedTimestamp: OffsetDateTime, body: Json)

case class Entity(id: EntityId, state: Json, systemName: SystemName)

case class Snapshot(id: SnapshotId, entityId: EntityId, systemName: SystemName,
                    timestamp: OffsetDateTime, body: Json)


case class Links(selfPageUrl: URI, firstPageUrl: URI,
                 nextPageUrl: Option[URI], previousPageUrl: Option[URI])

case class LinkedResponse(events: List[Event], pageNumber: Option[Long], pageSize: Int, links: Links)


object Event {
  def fromReceivedEvent(receivedEvent: ReceivedEvent) =
    Event(_: EventId, receivedEvent.entityId, receivedEvent.systemName, _: OffsetDateTime, receivedEvent.timestamp, receivedEvent.body)

  def replayEvents(events: List[Event]) = replayJsonEvents(events.map(_.body))

  def replayEventsWithSnapshot(snapshot: Option[Snapshot], events: List[Event]): Json =
    replayJsonEvents(snapshot.fold(Json())(_.body) :: events.map(_.body))

  def replayJsonEvents(events:List[Json]): Json =
    events.foldLeft(Json())(_ deepmerge _)

}
