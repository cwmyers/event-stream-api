package app.entity.snapshot

import java.time.OffsetDateTime

import app.action.AppAction.{generateSnapshotId, log, Script}
import app.action.EventStoreAction.saveSnapshot
import app.action.{AppAction, EventStoreAction}
import app.logging.SavedSnapshot
import app.model.{SystemName, EntityId, Event, Snapshot}

object SnapshotEntityService {
  def snapshot(entityId: EntityId, systemName: SystemName, time: OffsetDateTime):Script[Snapshot] =
    for {
      current <- EventStoreAction.listEventsByRange(entityId, systemName, None, time) map Event.replayEvents
      id <- generateSnapshotId
      s = Snapshot(id, entityId, systemName, time, current)
      _ <- saveSnapshot(s)
      _ <- log(SavedSnapshot(s))
    } yield s


}
