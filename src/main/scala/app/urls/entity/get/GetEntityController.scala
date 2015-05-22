package app.urls.entity.get

import app.action.AppAction.Script
import app.infrastructure._
import app.model.Codecs._
import app.model.{EntityId, SystemName}
import app.{MaybeTime, parseTime}
import argonaut.integrate.unfiltered.JsonResponse
import unfiltered.response.Ok

object GetEntityController {

  def get(id: EntityId, systemName: SystemName, request: FrameworkRequest): Script[FrameworkResponse] = {
    val maybeTime: MaybeTime = request.parameterValues("at").headOption.flatMap(parseTime)
    GetEntityService.getEntity(id, systemName, maybeTime) map
      (event => Ok ~> JsonResponse(event))
  }

}
