package app.entity.snapshot

import java.time.OffsetDateTime

import app.action.AppAction.Script
import app.action.{AppAction, EventStoreAction}
import app.model.{EntityId, Event, Snapshot}

object SnapshotEntityService {
  def snapshot(id: EntityId, time: OffsetDateTime):Script[Snapshot] =
    for {
      current <- EventStoreAction.listEventsForEntity(id, None, Some(time)) map Event.replayEvents
      id <- AppAction.generateSnapshotId
      s = Snapshot(id, time, current)
      _ <- EventStoreAction.saveSnapshot(s)
    } yield s


}
