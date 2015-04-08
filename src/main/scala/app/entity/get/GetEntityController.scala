package app.entity.get

import app.action.AppAction.Script
import app.action.EventStoreAction
import app.model.{Event, EntityId}
import argonaut.integrate.unfiltered.JsonResponse
import infrastructure.FrameworkResponse
import unfiltered.response.Ok
import argonaut._, Argonaut._
import scalaz._, Scalaz._

object GetEntityController {

  def get(id: EntityId): Script[FrameworkResponse] = {
    EventStoreAction.listEventsForEntity(id) map
      (events => merge(events)) map
      (event => Ok ~> JsonResponse(event))
  }

  private def merge(events: List[Event]): Json = {
    events.map(_.body).suml
  }

  implicit val jsonMonoid = new Monoid[Json] {
    override def zero: Json = Json()

    override def append(f1: Json, f2: => Json): Json = f1.deepmerge(f2)
  }
}
