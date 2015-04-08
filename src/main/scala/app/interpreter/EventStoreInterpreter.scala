package app.interpreter

import app.action.EventStoreAction

import scala.concurrent.Future

trait EventStoreInterpreter {

  def run[A](eventStoreAction: EventStoreAction[A]): Future[A]

}
