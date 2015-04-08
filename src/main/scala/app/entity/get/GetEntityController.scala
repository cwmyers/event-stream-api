package app.entity.get

import app.action.AppAction.Script
import app.model.EntityId
import argonaut.Argonaut._
import argonaut.integrate.unfiltered.JsonResponse
import infrastructure.FrameworkResponse
import unfiltered.response.Ok

object GetEntityController {

  def get(id: EntityId): Script[FrameworkResponse] = {
    GetEntityService.getEntity(id) map
      (event => Ok ~> JsonResponse(event))
  }

}
