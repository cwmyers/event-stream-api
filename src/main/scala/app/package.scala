import java.time.OffsetDateTime


import scala.util.Try

package object app {
  type ErrorOr[A] = Either[AppError, A]

  type MaybeTime = Option[OffsetDateTime]

  def parseTime(date: String) = Try(OffsetDateTime.parse(date.replace(" ", "+"))).toOption

}
