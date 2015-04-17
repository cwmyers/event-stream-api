import java.time.OffsetDateTime

import argonaut.Json

import scala.util.Try
import scalaz.{Monoid, \/}

package object app {
  type ErrorOr[A] = AppError \/ A

  type MaybeTime = Option[OffsetDateTime]

  def parseTime(date: String) = Try(OffsetDateTime.parse(date)).toOption

}
