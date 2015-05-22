package app.urls.entity.snapshot

import java.time.OffsetDateTime

import app.action.AppAction.{Script, generateSnapshotId, log}
import app.action.EventStoreAction.{listEventsByRange, saveSnapshot}
import app.logging.SavedSnapshot
import app.model.Event.replayEvents
import app.model.{EntityId, Snapshot, SystemName}

object SnapshotEntityService {
  def snapshot(entityId: EntityId, systemName: SystemName, time: OffsetDateTime): Script[Snapshot] =
    for {
      current <- listEventsByRange(entityId, systemName, None, time) map replayEvents
      id <- generateSnapshotId
      snapshot = Snapshot(id, entityId, systemName, time, current)
      _ <- saveSnapshot(snapshot)
      _ <- log(SavedSnapshot(snapshot))
    } yield snapshot
}
