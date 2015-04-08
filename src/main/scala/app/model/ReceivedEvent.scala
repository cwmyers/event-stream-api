package app.model

import java.time.OffsetDateTime
import argonaut.Json

case class EventId(id: String) extends AnyVal
case class EntityId(id: String) extends AnyVal

case class ReceivedEvent(entityId: EntityId, timestamp: OffsetDateTime, body: Json)

case class Event(id: EventId, entityId: EntityId,
                 createdTimestamp: OffsetDateTime,
                 suppliedTimestamp: OffsetDateTime, body: Json)

object Event {
  def fromReceivedEvent(receivedEvent: ReceivedEvent) =
    Event(_:EventId,receivedEvent.entityId, _:OffsetDateTime, receivedEvent.timestamp, receivedEvent.body)
}
