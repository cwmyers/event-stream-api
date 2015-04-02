package app.action

import app.action.AppAction.Script
import app.model.{EventId, Event}

import scalaz.Free._
import scalaz._

sealed trait AppAction[A] {
  def map[B](f: A => B): AppAction[B] = this match {
//    case DecodeAction(body, decoder, onResult) => DecodeAction(body, decoder, onResult andThen f)
    case SaveEvent(event, onResult) => SaveEvent(event, f compose onResult)
  }

}

//case class DecodeAction[T, A](body: String, decoder: String => T, onResult: T => A) extends AppAction[A]

sealed trait EventStoreAction[A]

case class SaveEvent[A](event: Event, onResult: EventId => A ) extends AppAction[A] with EventStoreAction[A]


object AppAction {
  type Script[A] = Free[AppAction, A]

  implicit def appActionFunctor: Functor[AppAction] = new Functor[AppAction] {
    override def map[A, B](fa: AppAction[A])(f: (A) => B): AppAction[B] = fa map f
  }

//  def decode[T](body: String, decoder: String => T) = liftF(DecodeAction[T,T](body, decoder, identity))
  def noAction[A](a:A): Script[A]= Free.pure(a)
}


object EventStoreAction {
  def saveEvent(event: Event):Script[EventId] = liftF(SaveEvent(event, identity))
}

