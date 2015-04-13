import java.time.OffsetDateTime

import argonaut.Json

import scala.util.Try
import scalaz.{Monoid, \/}

package object app {
  type ErrorOr[A] = AppError \/ A

  type MaybeTime = Option[OffsetDateTime]

  def parseTime(date: String) = Try(OffsetDateTime.parse(date)).toOption

  implicit val jsonMonoid = new Monoid[Json] {
    override def zero: Json = Json()

    override def append(f1: Json, f2: => Json): Json = f1 deepmerge f2
  }


}
