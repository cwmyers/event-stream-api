import scalaz.\/

package object app {
  type ErrorOr[A] = AppError \/ A

}
