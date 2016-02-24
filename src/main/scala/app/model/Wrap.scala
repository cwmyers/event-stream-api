package app.model

import simulacrum.typeclass
import scala.language.implicitConversions

@typeclass trait Wrap[A] {
  def wrap(s:String): A
  def unwrap(a:A): String
}
