package app.entity.get

import app.MaybeTime
import app.action.AppAction
import app.action.EventStoreAction._
import app.logging.GetEntityLog
import app.model.{Entity, SystemName, EntityId}
import app.model.Event.replayEventsWithSnapshot

object GetEntityService {

  def getEntity(id: EntityId, systemName: SystemName, time: MaybeTime) = {
    for {
      currentTime <- AppAction.currentTime
      to = time.getOrElse(currentTime)
      snapshot <- getLatestSnapshotBefore(id, systemName, to)
      events <- listEventsByRange(id, systemName, snapshot.map(_.timestamp), to)
      entity = replayEventsWithSnapshot(snapshot, events)
      _ <- AppAction.log(GetEntityLog(to, snapshot, events, entity))
    } yield Entity(id, entity, systemName)
  }

}
