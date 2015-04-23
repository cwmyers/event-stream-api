package app.entity.snapshot

import java.time.OffsetDateTime

import app.action.AppAction.Script
import app.action.{AppAction, EventStoreAction}
import app.model.{SystemName, EntityId, Event, Snapshot}

object SnapshotEntityService {
  def snapshot(entityId: EntityId, systemName: SystemName, time: OffsetDateTime):Script[Snapshot] =
    for {
      current <- EventStoreAction.listEventsByRange(entityId, systemName, None, time) map Event.replayEvents
      id <- AppAction.generateSnapshotId
      s = Snapshot(id, entityId, systemName, time, current)
      _ <- EventStoreAction.saveSnapshot(s)
    } yield s


}
