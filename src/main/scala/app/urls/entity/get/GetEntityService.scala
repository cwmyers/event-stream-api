package app.urls.entity.get

import app.MaybeTime
import app.action.AppAction.{currentTime, log}
import app.action.EventStoreAction._
import app.logging.GetEntityLog
import app.model.Event.replayEventsWithSnapshot
import app.model.{Entity, EntityId, SystemName}

object GetEntityService {

  def getEntity(id: EntityId, systemName: SystemName, time: MaybeTime) = {
    for {
      now <- currentTime
      to = time.getOrElse(now)
      maybeSnapshot <- getLatestSnapshotBefore(id, systemName, to)
      events <- listEventsByRange(id, systemName, maybeSnapshot.map(_.timestamp), to)
      jsonBody = replayEventsWithSnapshot(maybeSnapshot, events)
      entity = Entity(id, jsonBody, systemName)
      _ <- log(GetEntityLog(to, maybeSnapshot, events, entity))
    } yield entity
  }

}
