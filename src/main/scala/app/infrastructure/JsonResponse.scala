package app.infrastructure

import io.circe.Encoder
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import unfiltered.response.JsonContent

object JsonResponse {
  def apply[A: Encoder](a: A) =
    JsonContent ~> responseString(a.asJson.noSpaces)
}