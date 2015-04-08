package app.action

import java.time.OffsetDateTime

import app.action.AppAction.Script
import app.model.{EntityId, Event, EventId}

import scalaz.Free._
import scalaz._

sealed trait AppAction[A] {
  def map[B](f: A => B): AppAction[B] = this match {
    case SaveEvent(event, next) => SaveEvent(event, f(next))
    case ListEvents(onResult) => ListEvents(onResult andThen f)
    case GenerateId(onResult) => GenerateId(onResult andThen f)
    case CurrentTime(onResult) => CurrentTime(onResult andThen f)
    case ListEventsForEntity(id, onResult) => ListEventsForEntity(id, onResult andThen f)
  }

  def lift: Script[A] = liftF(this)
}

case class GenerateId[A](onResult: EventId => A) extends AppAction[A]
case class CurrentTime[A](onResult: OffsetDateTime => A) extends AppAction[A]

sealed trait EventStoreAction[A]
case class SaveEvent[A](event: Event, next: A) extends AppAction[A] with EventStoreAction[A]
case class ListEvents[A](onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class ListEventsForEntity[A](id:EntityId, onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]


object AppAction {
  type Script[A] = Free[AppAction, A]

  implicit def appActionFunctor: Functor[AppAction] = new Functor[AppAction] {
    override def map[A, B](fa: AppAction[A])(f: (A) => B): AppAction[B] = fa map f
  }

  def noAction[A](a: A): Script[A] = Free.pure(a)
  def generateId: Script[EventId] = GenerateId(identity).lift
  def currentTime: Script[OffsetDateTime] = CurrentTime(identity).lift
}


object EventStoreAction {
  def saveEvent(event: Event): Script[Unit] = SaveEvent(event, ()).lift
  def listEvents: Script[List[Event]] = ListEvents(identity).lift
  def listEventsForEntity(id: EntityId): Script[List[Event]] = ListEventsForEntity(id, identity).lift
}

