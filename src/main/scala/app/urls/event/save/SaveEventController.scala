package app.urls.event.save

import app.action.AppAction._
import app.infrastructure._
import app.model.Codecs._
import app.model.ReceivedEvent
import io.circe.parser._
import unfiltered.response.{BadRequest, Created}

object SaveEventController {

  def save(request: FrameworkRequest): Script[FrameworkResponse] = {

    val maybeEvent = decode[ReceivedEvent](request.body)

    val saveEventAction = maybeEvent map SaveEventService.save
    saveEventAction.fold[Script[FrameworkResponse]](
      error => noAction(BadRequest ~> responseString(error.getMessage)),
      action => action.map(event => Created ~> JsonResponse(event))
    )

  }

}
