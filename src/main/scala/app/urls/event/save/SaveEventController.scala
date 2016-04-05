package app.urls.event.save

import app.action.AppAction._
import app.infrastructure
import app.model.Codecs._
import app.model.ReceivedEvent
import infrastructure._
import unfiltered.response.{BadRequest, Created, ResponseString}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object SaveEventController {

  def save(request: FrameworkRequest): Script[FrameworkResponse] = {

    val maybeEvent = decode[ReceivedEvent](request.body)

    val saveEventAction = maybeEvent map SaveEventService.save
    saveEventAction.fold[Script[FrameworkResponse]](error => noAction(BadRequest ~> responseString(error.getMessage)),
      action => action.map(event => Created ~> JsonResponse(event)))

  }

}
