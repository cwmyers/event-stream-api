package app.infrastructure

import io.circe.Encoder
import io.circe.syntax._
import unfiltered.response.JsonContent

object JsonResponse {
  def apply[A: Encoder](a: A) =
    JsonContent ~> responseString(a.asJson.noSpaces)
}
