package app

import app.action.AppAction.Script
import app.infrastructure.{FrameworkRequest, FrameworkResponse}
import app.model.{EntityId, SystemName}
import app.urls.entity.get.GetEntityController
import app.urls.entity.snapshot.SnapshotEntityController
import app.urls.event.list.ListEventsController
import app.urls.event.save.SaveEventController
import unfiltered.request.{GET, POST, Path, Seg}
import unfiltered.response.ResponseHeader

object Routes {

  type AppRoutes = PartialFunction[FrameworkRequest, Script[FrameworkResponse]]

  def appRoutes: AppRoutes = {
    case req @ POST(Path("/event"))            => SaveEventController.save(req)
    case req @ GET(Path(Seg("events" :: Nil))) => ListEventsController.list(req)
    case req @ GET(Path(Seg("events" :: entityId :: Nil))) =>
      ListEventsController.listForEntity(req, EntityId(entityId))
    case req @ GET(Path(Seg("entity" :: entityId :: Nil))) =>
      GetEntityController.get(EntityId(entityId), req)
    case req @ POST(Path(Seg("snapshot" :: entityId :: systemName :: time :: Nil))) =>
      SnapshotEntityController.snapshot(EntityId(entityId), SystemName(systemName), time)

  }

  def withHeaders(appRoutes: AppRoutes): AppRoutes = {
    case req if appRoutes.isDefinedAt(req) =>
      appRoutes(req).map(r => r ~> ResponseHeader("Access-Control-Allow-Origin", Set("*")))
  }

}
