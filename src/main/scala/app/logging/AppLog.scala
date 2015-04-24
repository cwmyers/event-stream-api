package app.logging

import java.time.OffsetDateTime

import app.model.{Entity, Event, Snapshot}

sealed trait AppLog
case class GetEntityLog(requestedTime: OffsetDateTime, snapshot: Option[Snapshot],
                        events: List[Event], entity:Entity) extends AppLog
case class SavedSnapshot(snapshot: Snapshot) extends AppLog
case class SavedEvent(event: Event) extends AppLog
