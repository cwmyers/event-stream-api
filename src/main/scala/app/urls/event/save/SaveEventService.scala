package app.urls.event.save

import app.action.AppAction._
import app.action.EventStoreAction._
import app.logging.SavedEvent
import app.model.{Event, ReceivedEvent}

object SaveEventService {
  def save(receivedEvent: ReceivedEvent): Script[Event] = for {
    id <- generateEventId
    timestamp <- currentTime
    event = Event.fromReceivedEvent(receivedEvent)(id, timestamp)
    _ <- saveEvent(event)
    _ <- log(SavedEvent(event))
  } yield event
}
