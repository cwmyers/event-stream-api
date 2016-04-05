package app.urls.entity.get

import app.action.AppAction.{Script, noAction}
import app.infrastructure._
import app.model.Codecs._
import app.model.{EntityId, SystemName}
import app.{MaybeTime, parseTime}
import unfiltered.response.{BadRequest, Ok}
import cats.syntax.all._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object GetEntityController {

  def get(id: EntityId, request: FrameworkRequest): Script[FrameworkResponse] = {
    val maybeSystemNames = request.parameterValues("systemNames").toList.flatMap(_.split("\\s*,\\s*")).map(SystemName).toNel
    val maybeTime: MaybeTime = request.parameterValues("at").headOption.flatMap(parseTime)
    maybeSystemNames.fold(noAction[FrameworkResponse](BadRequest))(systemNames =>
    GetEntityService.getEntity(id, systemNames, maybeTime) map
      (event => Ok ~> JsonResponse(event))
    )
  }

}
