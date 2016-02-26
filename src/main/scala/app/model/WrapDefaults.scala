package app.model

import scalaz.Monoid

object WrapDefaults {

  type WrapString[A] = Wrap[A, String]
  type WrapInt[A] = Wrap[A, Int]
  type WrapLong[A] = Wrap[A, Long]
  type WrapDouble[A] = Wrap[A, Double]
  type WrapFloat[A] = Wrap[A, Float]
  type WrapByte[A] = Wrap[A, Byte]
  type WrapBoolean[A] = Wrap[A, Boolean]

  import shapeless._

  implicit val stringWrap: WrapString[String] = new WrapString[String] {
    override def wrap(a: String): String = a

    override def unwrap(a: String): String = a
  }

  implicit val intWrap: WrapInt[Int] = new WrapInt[Int] {
    override def wrap(b: Int): Int = b

    override def unwrap(a: Int): Int = a
  }

  implicit val longWrap: WrapLong[Long] = new WrapLong[Long] {
    override def wrap(b: Long): Long = b

    override def unwrap(a: Long): Long = a
  }

  implicit val doubleWrap: WrapDouble[Double] = new WrapDouble[Double] {
    override def wrap(b: Double): Double = b

    override def unwrap(a: Double): Double = a
  }

  implicit val floatWrap: WrapFloat[Float] = new WrapFloat[Float] {
    override def wrap(b: Float): Float = b

    override def unwrap(a: Float): Float = a
  }

  implicit val byteWrap: WrapByte[Byte] = new WrapByte[Byte] {
    override def wrap(b: Byte): Byte = b

    override def unwrap(a: Byte): Byte = a
  }

  implicit val booleanWrap: WrapBoolean[Boolean] = new WrapBoolean[Boolean] {
    override def wrap(b: Boolean): Boolean = b

    override def unwrap(a: Boolean): Boolean = a
  }


  implicit def hnilUnwrapper[B: Monoid]: Wrap[HNil, B] = new Wrap[HNil, B] {
    override def wrap(s: B): HNil = HNil

    override def unwrap(a: HNil): B = implicitly[Monoid[B]].zero
  }

  implicit def hconsUnwrapper[H, T <: HList, B](implicit wrap1: Wrap[H, B], wrap2: Wrap[T, B]): Wrap[H :: T, B] = new Wrap[H :: T, B] {
    override def wrap(b: B): ::[H, T] = {
      val hWrap: Wrap[H, B] = wrap1
      val tWrap: Wrap[T, B] = wrap2
      hWrap.wrap(b) :: tWrap.wrap(b)
    }

    override def unwrap(a: ::[H, T]): B = {
      implicitly[Wrap[H, B]].unwrap(a.head)
    }

  }

  implicit def caseClassParser[A, R <: HList, B](implicit
                                                 gen: Generic[A] {type Repr = R},
                                                 reprWrap: Wrap[R, B]
                                                ): Wrap[A, B] = new Wrap[A, B] {
    override def wrap(s: B): A = gen.from(reprWrap.wrap(s))

    override def unwrap(a: A): B = reprWrap.unwrap(gen.to(a))
  }


}
