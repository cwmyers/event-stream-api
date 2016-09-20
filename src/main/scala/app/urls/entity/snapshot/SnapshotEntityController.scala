package app.urls.entity.snapshot

import app.action.AppAction.{Script, noAction}
import app.infrastructure.{FrameworkResponse, JsonResponse, responseString}
import app.model.Codecs._
import app.model.{EntityId, SystemName}
import app.parseTime
import unfiltered.response.{BadRequest, Created}

object SnapshotEntityController {

  def snapshot(id: EntityId, systemName: SystemName, time: String): Script[FrameworkResponse] = {
    val error = BadRequest ~> responseString("Time must be in ISO8601 format")
    parseTime(time).fold(noAction[FrameworkResponse](error))(
      dateTime =>
        SnapshotEntityService
          .snapshot(id, systemName, dateTime) map (snapshot => Created ~> JsonResponse(snapshot))
    )
  }

}
