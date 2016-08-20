package app.interpreter

import java.sql.Timestamp
import java.time.{OffsetDateTime, ZoneOffset}

import app.model._
import io.circe._
import io.circe.parser._

package object sql {
  def fromTimestamp(ts: java.sql.Timestamp): OffsetDateTime = OffsetDateTime.ofInstant(ts.toInstant, ZoneOffset.UTC)

  def fromOffsetDateTime(date: OffsetDateTime) = Timestamp.from(date.toInstant)

  def createSnapshot(id: String, entityId: String, systemName: String,
                     timestamp: Timestamp, body: String): Snapshot =
    Snapshot(SnapshotId(id), EntityId(entityId),
      SystemName(systemName), fromTimestamp(timestamp), parse(body).getOrElse(Json.Null))

  def snapshotToFields(snapshot: Snapshot): SnapshotsTable.Fields =
    (snapshot.id, snapshot.entityId, snapshot.systemName, fromOffsetDateTime(snapshot.timestamp), snapshot.body.noSpaces)

  def eventToFields(event: Event): EventsTable.Fields =
    (event.id, event.entityId, event.systemName,
      fromOffsetDateTime(event.createdTimestamp), fromOffsetDateTime(event.suppliedTimestamp), event.body.noSpaces)

  def createEvent(id:String, entityId:String, systemName:String, createdTimestamp:Timestamp, suppliedTimestamp:Timestamp,body:String) =
  Event(EventId(id), EntityId(entityId), SystemName(systemName), fromTimestamp(createdTimestamp), fromTimestamp(suppliedTimestamp), parse(body).getOrElse(Json.Null))
}
