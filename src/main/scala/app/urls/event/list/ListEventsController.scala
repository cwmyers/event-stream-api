package app.urls.event.list

import app.action.AppAction.Script
import app.infrastructure.{FrameworkRequest, FrameworkResponse, JsonResponse}
import app.model.Codecs._
import app.model.{EntityId, SystemName}
import app.parseTime
import cats.syntax.all._
import mouse.all._
import unfiltered.response.Ok

object ListEventsController {

  def list(request: FrameworkRequest): Script[FrameworkResponse] =
    getEvents(request, None)

  def listForEntity(request: FrameworkRequest, entityId: EntityId): Script[FrameworkResponse] =
    getEvents(request, entityId.some)

  def getEvents(request: FrameworkRequest, entityId: Option[EntityId]): Script[FrameworkResponse] = {
    val pageSize   = getFromRequest(request, "pageSize").flatMap(_.parseIntOption)
    val pageNumber = getFromRequest(request, "pageNumber").flatMap(_.parseLongOption)
    val systemName = getFromRequest(request, "systemName").map(SystemName)
    val fromTime   = getFromRequest(request, "from").flatMap(parseTime)
    val toTime     = getFromRequest(request, "to").flatMap(parseTime)
    ListEventsService
      .getEvents(entityId, systemName, fromTime, toTime, pageSize, pageNumber)
      .map(events => Ok ~> JsonResponse(events))
  }

  def getFromRequest(request: FrameworkRequest, param: String): Option[String] =
    request.parameterValues(param).headOption
}
