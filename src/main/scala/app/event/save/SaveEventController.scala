package app.event.save

import app.action.AppAction._
import app.action.EventStoreAction
import app.model.Codecs._
import app.model.Event
import argonaut.Argonaut._
import infrastructure._
import unfiltered.response.{BadRequest, Created, ResponseString}

object SaveEventController {

  def save(request: FrameworkRequest): Script[FrameworkResponse] = {

//    for {
//      event <- AppAction.decode[Event](request.body, _ => Event(OffsetDateTime.now(), Json("a" := "a")))
//      _ <- EventStoreAction.saveEvent(event)
//    } yield Created

    val maybeEvent = request.body.decodeEither[Event]
    val saveEventAction = maybeEvent map EventStoreAction.saveEvent
    saveEventAction.fold[Script[FrameworkResponse]](a => noAction(BadRequest ~> ResponseString(a)),
      action => action.map(a => Created ~> ResponseString(a.id)))

  }

}
