package app.urls.event.list

import app.action.AppAction.Script
import app.infrastructure.{JsonResponse, FrameworkRequest, FrameworkResponse}
import app.model.Codecs._
import app.model.{SystemName, EntityId}
import cats.data.Xor
import unfiltered.response.Ok

import cats.syntax.all._

object ListEventsController {

  def list(request: FrameworkRequest): Script[FrameworkResponse] =
    getEvents(request, None)

  def listForEntity(request: FrameworkRequest, entityId: EntityId): Script[FrameworkResponse] =
    getEvents(request, entityId.some)

  def getEvents(request: FrameworkRequest, entityId: Option[EntityId]): Script[FrameworkResponse] = {
    val pageSize = getFromRequest(request, "pageSize").flatMap(parseInt)
    val pageNumber = getFromRequest(request, "pageNumber").flatMap(parseLong)
    val systemName = getFromRequest(request, "systemName").map(SystemName)
    ListEventsService.getEvents(entityId, systemName, pageSize, pageNumber) map (events => Ok ~> JsonResponse(events))
  }

  def getFromRequest(request: FrameworkRequest, param: String): Option[String] =
    request.parameterValues(param).headOption

  def parseInt(s: String): Option[Int] = Xor.catchOnly[NumberFormatException](s.toInt).toOption
  def parseLong(s: String): Option[Long] = Xor.catchOnly[NumberFormatException](s.toLong).toOption
}
