package app.event.save

import app.action.AppAction._
import app.action.EventStoreAction._
import app.model.Codecs._
import app.model.{Event, ReceivedEvent}
import argonaut.Argonaut._
import argonaut.integrate.unfiltered.JsonResponse
import infrastructure._
import unfiltered.response.{BadRequest, Created, ResponseString}

object SaveEventController {

  def save(request: FrameworkRequest): Script[FrameworkResponse] = {

    val maybeEvent = request.body.decodeEither[ReceivedEvent]

    def script(receivedEvent: ReceivedEvent) =
      for {
        id <- generateId
        timestamp <- currentTime
        event = Event(id, timestamp, receivedEvent.timestamp, receivedEvent.body)
        _ <- saveEvent(event)
      } yield event

    val saveEventAction = maybeEvent map script
    saveEventAction.fold[Script[FrameworkResponse]](a => noAction(BadRequest ~> ResponseString(a)),
      action => action.map(event => Created ~> JsonResponse(event)))

  }

}
