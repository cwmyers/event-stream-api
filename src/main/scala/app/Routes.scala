package app

import app.action.AppAction.Script
import app.urls.entity.get.GetEntityController
import app.urls.entity.snapshot.SnapshotEntityController
import app.urls.event.list.ListEventsController
import app.urls.event.save.SaveEventController
import app.infrastructure.{FrameworkResponse, FrameworkRequest}
import app.model.{SystemName, EntityId}
import unfiltered.request.{Seg, GET, POST, Path}

object Routes {

  type AppRoutes = PartialFunction[FrameworkRequest, Script[FrameworkResponse]]

  def appRoutes: AppRoutes = {
    case req@POST(Path("/event")) => SaveEventController.save(req)
    case req@GET(Path(Seg("events" :: Nil))) => ListEventsController.list(req)
    case req@GET(Path(Seg("events" :: entityId ::  Nil))) => ListEventsController.listForEntity(req, EntityId(entityId))
    case req@GET(Path(Seg("entity" :: entityId :: systemName :: Nil))) => GetEntityController.get(EntityId(entityId),SystemName(systemName), req)
    case req@POST(Path(Seg("snapshot" :: entityId :: systemName :: time ::  Nil))) => SnapshotEntityController.snapshot(EntityId(entityId), SystemName(systemName), time)

  }

}
