package app.entity.get

import app.MaybeTime
import app.action.EventStoreAction
import app.model.EntityId
import app.model.Event.replayEvents

object GetEntityService {

  def getEntity(id: EntityId, time: MaybeTime) =
    EventStoreAction.listEventsByRange(id, None, time) map
      (events => replayEvents(events))

}
