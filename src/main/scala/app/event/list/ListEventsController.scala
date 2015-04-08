package app.event.list

import app.action.AppAction.Script
import app.action.EventStoreAction
import app.infrastructure.FrameworkResponse
import app.model.Codecs._
import argonaut.integrate.unfiltered.JsonResponse
import unfiltered.response.Ok

object ListEventsController {

  def list: Script[FrameworkResponse] = {
    EventStoreAction.listEvents map (events => Ok ~> JsonResponse(events))
  }

}
