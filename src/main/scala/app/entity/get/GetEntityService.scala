package app.entity.get

import app.MaybeTime
import app.action.EventStoreAction
import app.model.{EntityId, Event}
import argonaut.Json

import scalaz.Scalaz._
import scalaz._

object GetEntityService {

  def getEntity(id: EntityId, time:MaybeTime) =
    EventStoreAction.listEventsForEntity(id, None, time) map
      (events => replayEvents(events))

  private def replayEvents(events: List[Event]): Json =
    events.map(_.body).suml


  implicit val jsonMonoid = new Monoid[Json] {
    override def zero: Json = Json()

    override def append(f1: Json, f2: => Json): Json = f1 deepmerge f2
  }

}
