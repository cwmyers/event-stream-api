package app.entity.get

import app.action.EventStoreAction
import app.model.{Event, EntityId}
import argonaut.Json

import scalaz._, Scalaz._

object GetEntityService {

  def getEntity(id: EntityId) =
    EventStoreAction.listEventsForEntity(id) map
      (events => replayEvents(events))

  private def replayEvents(events: List[Event]): Json =
    events.map(_.body).suml


  implicit val jsonMonoid = new Monoid[Json] {
    override def zero: Json = Json()

    override def append(f1: Json, f2: => Json): Json = f1.deepmerge(f2)
  }

}
