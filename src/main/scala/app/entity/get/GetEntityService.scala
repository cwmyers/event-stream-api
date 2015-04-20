package app.entity.get

import app.MaybeTime
import app.action.AppAction
import app.action.EventStoreAction._
import app.logging.GetEntityLog
import app.model.EntityId
import app.model.Event.replayEventsWithSnapshot

object GetEntityService {

  def getEntity(id: EntityId, time: MaybeTime) = {
    for {
      currentTime <- AppAction.currentTime
      to = time.getOrElse(currentTime)
      snapshot <- getLatestSnapshotBefore(id, to)
      events <- listEventsByRange(id, snapshot.map(_.timestamp), to)
      entity = replayEventsWithSnapshot(snapshot, events)
      _ <- AppAction.log(GetEntityLog(to, snapshot, events, entity))
    } yield entity
  }

}
