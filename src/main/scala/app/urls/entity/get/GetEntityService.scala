package app.urls.entity.get

import app.MaybeTime
import app.action.AppAction.{Script, currentTime}
import app.action.EventStoreAction._
import app.model.Event.replayEventsWithSnapshot
import app.model.{Entity, EntityId, State, SystemName}
import cats.data.NonEmptyList
import cats.std.all._
import cats.syntax.all._

object GetEntityService {

  def getEntity(id: EntityId, systemNames: NonEmptyList[SystemName], time: MaybeTime) = {
    val allState: NonEmptyList[Script[State]] = systemNames.map { systemName =>
      for {
        now <- currentTime
        to = time.getOrElse(now)
        maybeSnapshot <- getLatestSnapshotBefore(id, systemName, to)
        events <- listEventsByRange(id, systemName, maybeSnapshot.map(_.timestamp), to)
        jsonBody = replayEventsWithSnapshot(maybeSnapshot, events)
        entityStateForSystem = State(systemName, jsonBody)
      //        _ <- log(GetEntityLog(to, maybeSnapshot, events, entityStateForSystem))
      } yield entityStateForSystem
    }
    val script: Script[NonEmptyList[State]] = allState.sequence

    script.map(l => Entity(id, l.unwrap.toSet))
  }

}
