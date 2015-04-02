package app.model

import java.time.OffsetDateTime
import argonaut.Json

case class EventId(id: String)

case class ReceivedEvent(timestamp: OffsetDateTime, body: Json)

case class Event(id: EventId, createdTimestamp: OffsetDateTime, suppliedTimestamp: OffsetDateTime, body: Json)
