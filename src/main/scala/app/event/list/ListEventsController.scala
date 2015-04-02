package app.event.list

import app.action.AppAction.Script
import app.action.EventStoreAction
import app.model.Codecs._
import argonaut.integrate.unfiltered.JsonResponse
import infrastructure.FrameworkResponse
import unfiltered.response.Ok

object ListEventsController {

  def list: Script[FrameworkResponse] = {
    EventStoreAction.listEvents map (events => Ok ~> JsonResponse(events))
  }

}
