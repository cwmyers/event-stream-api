package app.entity.get

import app.action.AppAction.Script
import app.infrastructure._
import app.model.EntityId
import app.{MaybeTime, parseTime}
import argonaut.Argonaut._
import argonaut.integrate.unfiltered.JsonResponse
import unfiltered.response.Ok

object GetEntityController {

  def get(id: EntityId, request: FrameworkRequest): Script[FrameworkResponse] = {
    val maybeTime: MaybeTime = request.parameterValues("at").headOption.flatMap(parseTime)
    GetEntityService.getEntity(id, maybeTime) map
      (event => Ok ~> JsonResponse(event))
  }

}
