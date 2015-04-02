package app

import _root_.infrastructure.{FrameworkRequest, FrameworkResponse}
import app.action.AppAction.Script
import app.event.list.ListEventsController
import app.event.save.SaveEventController
import unfiltered.request.{GET, POST, Path}

object Routes {

  type AppRoutes = PartialFunction[FrameworkRequest, Script[FrameworkResponse]]

  def appRoutes: AppRoutes = {
    case req@POST(Path("/event")) => SaveEventController.save(req)
    case GET(Path("/event"))      => ListEventsController.list
  }

}
