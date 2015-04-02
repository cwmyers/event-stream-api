package app.interpreter

import app.action.AppAction.Script
import infrastructure._

import scala.concurrent.Future

trait AppInterpreter {

  def run(appAction: Script[FrameworkResponse]): Future[FrameworkResponse]


}
