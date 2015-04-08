package app.entity.get

import app.action.AppAction.Script
import app.infrastructure.FrameworkResponse
import app.model.EntityId
import argonaut.Argonaut._
import argonaut.integrate.unfiltered.JsonResponse
import unfiltered.response.Ok

object GetEntityController {

  def get(id: EntityId): Script[FrameworkResponse] = {
    GetEntityService.getEntity(id) map
      (event => Ok ~> JsonResponse(event))
  }

}
