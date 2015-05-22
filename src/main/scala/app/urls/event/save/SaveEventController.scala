package app.urls.event.save

import app.action.AppAction._
import app.infrastructure
import app.model.Codecs._
import app.model.ReceivedEvent
import argonaut.Argonaut._
import argonaut.integrate.unfiltered.JsonResponse
import infrastructure._
import unfiltered.response.{BadRequest, Created, ResponseString}

object SaveEventController {

  def save(request: FrameworkRequest): Script[FrameworkResponse] = {

    val maybeEvent = request.body.decodeEither[ReceivedEvent]

    val saveEventAction = maybeEvent map SaveEventService.save
    saveEventAction.fold[Script[FrameworkResponse]](error => noAction(BadRequest ~> ResponseString(error)),
      action => action.map(event => Created ~> JsonResponse(event)))

  }

}
