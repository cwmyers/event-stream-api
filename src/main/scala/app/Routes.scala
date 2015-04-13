package app

import app.action.AppAction.Script
import app.action.SaveSnapshot
import app.entity.get.GetEntityController
import app.entity.snapshot.SnapshotEntityController
import app.event.list.ListEventsController
import app.event.save.SaveEventController
import app.infrastructure.{FrameworkResponse, FrameworkRequest}
import app.model.EntityId
import unfiltered.request.{Seg, GET, POST, Path}

object Routes {

  type AppRoutes = PartialFunction[FrameworkRequest, Script[FrameworkResponse]]

  def appRoutes: AppRoutes = {
    case req@POST(Path("/event")) => SaveEventController.save(req)
    case GET(Path("/event")) => ListEventsController.list
    case req@GET(Path(Seg("entity" :: entityId :: Nil))) => GetEntityController.get(EntityId(entityId), req)
    case req@POST(Path(Seg("snapshot" :: entityId :: time ::  Nil))) => SnapshotEntityController.snapshot(EntityId(entityId), time)

  }

}
