package app.model

import Wrap.ops._

object WrapDefaults {

  import shapeless._

  implicit val stringUnwrapper: Wrap[String] = new Wrap[String] {
    override def wrap(a: String): String = a

    override def unwrap(a: String): String = a
  }

  implicit val hnilUnwrapper: Wrap[HNil] = new Wrap[HNil] {
    override def wrap(s: String): HNil = HNil

    override def unwrap(a: HNil): String = ""
  }

  implicit def hconsUnwrapper[H: Wrap, T <: HList : Wrap]: Wrap[H :: T] = new Wrap[H :: T] {
    override def wrap(s: String): ::[H, T] = {
      val hWrap: Wrap[H] = implicitly[Wrap[H]]
      val tWrap: Wrap[T] = implicitly[Wrap[T]]
      hWrap.wrap(s) :: tWrap.wrap(s)
    }

    override def unwrap(a: ::[H, T]): String = {
      a.head.unwrap
    }

  }

  implicit def caseClassParser[A, R <: HList](implicit
                                              gen: Generic[A] {type Repr = R},
                                              reprWrap: Wrap[R]
                                             ): Wrap[A] = new Wrap[A] {
    override def wrap(s: String): A = gen.from(reprWrap.wrap(s))

    override def unwrap(a: A): String = reprWrap.unwrap(gen.to(a))
  }


}
