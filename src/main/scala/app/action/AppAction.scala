package app.action

import java.time.OffsetDateTime

import app.MaybeTime
import app.action.AppAction.Script
import app.model._

import scalaz.Free._
import scalaz._

sealed trait AppAction[A] {
  def map[B](f: A => B): AppAction[B] = this match {
    case SaveEvent(event, next) => SaveEvent(event, f(next))
    case ListEvents(onResult) => ListEvents(onResult andThen f)
    case GenerateId(onResult) => GenerateId(onResult andThen f)
    case CurrentTime(onResult) => CurrentTime(onResult andThen f)
    case ListEventsForEntity(id, from, to, onResult) => ListEventsForEntity(id, from, to, onResult andThen f)
    case SaveSnapshot(snapshot, next) => SaveSnapshot(snapshot, f(next))
  }

  def lift: Script[A] = liftF(this)
}

case class GenerateId[A](onResult: String => A) extends AppAction[A]
case class CurrentTime[A](onResult: OffsetDateTime => A) extends AppAction[A]

sealed trait EventStoreAction[A]
case class SaveEvent[A](event: Event, next: A) extends AppAction[A] with EventStoreAction[A]
case class ListEvents[A](onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class ListEventsForEntity[A](id:EntityId, from: Option[OffsetDateTime], to:Option[OffsetDateTime], onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]

case class SaveSnapshot[A](snapshot: Snapshot, next: A) extends AppAction[A] with EventStoreAction[A]


object AppAction {
  type Script[A] = Free[AppAction, A]

  implicit val appActionFunctor: Functor[AppAction] = new Functor[AppAction] {
    override def map[A, B](fa: AppAction[A])(f: (A) => B): AppAction[B] = fa map f
  }

  def noAction[A](a: A): Script[A] = Free.pure(a)
  def generateId: Script[String] = GenerateId(identity).lift
  def generateEventId: Script[EventId] = generateId map EventId
  def generateSnapshotId: Script[SnapshotId] = generateId map SnapshotId

  def currentTime: Script[OffsetDateTime] = CurrentTime(identity).lift
}


object EventStoreAction {
  def saveEvent(event: Event): Script[Unit] = SaveEvent(event, ()).lift
  def listEvents: Script[List[Event]] = ListEvents(identity).lift
  def listEventsForEntity(id: EntityId, from: MaybeTime = None, to: MaybeTime = None): Script[List[Event]] =
    ListEventsForEntity(id, from, to, identity).lift

  def saveSnapshot(snapshot: Snapshot): Script[Unit] = SaveSnapshot(snapshot, ()).lift
}

