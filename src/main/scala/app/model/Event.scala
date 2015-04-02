package app.model

import java.time.OffsetDateTime

import argonaut.Json

case class EventId(id: String)

case class Event(id: Option[EventId], timestamp: OffsetDateTime, body: Json)
