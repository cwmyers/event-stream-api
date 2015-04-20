package app.logging

import java.time.OffsetDateTime

import app.model.{Event, Snapshot}
import argonaut.Json

sealed trait AppLog
case class GetEntityLog(requestedTime: OffsetDateTime, snapshot: Option[Snapshot], events: List[Event], entity:Json) extends AppLog
