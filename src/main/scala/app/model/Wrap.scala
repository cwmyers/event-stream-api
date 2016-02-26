package app.model

trait Wrap[A, B] {
  def wrap(b: B): A
  def unwrap(a: A): B
}
