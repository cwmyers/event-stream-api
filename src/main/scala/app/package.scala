import java.time.OffsetDateTime

import cats.data.Xor

import scala.util.Try

package object app {
  type ErrorOr[A] = AppError Xor A

  type MaybeTime = Option[OffsetDateTime]

  def parseTime(date: String) = Try(OffsetDateTime.parse(date.replace(" ", "+"))).toOption

}
