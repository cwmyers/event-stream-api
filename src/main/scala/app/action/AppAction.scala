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
    case ListEvents(entityId, pageSize, pageNumber, onResult) => ListEvents(entityId, pageSize, pageNumber, onResult andThen f)
    case GenerateId(onResult) => GenerateId(onResult andThen f)
    case CurrentTime(onResult) => CurrentTime(onResult andThen f)
    case ListEventsByRange(id, from, to, onResult) => ListEventsByRange(id, from, to, onResult andThen f)
    case SaveSnapshot(snapshot, next) => SaveSnapshot(snapshot, f(next))
    case GetEventsCount(entityId, onResult) => GetEventsCount(entityId, onResult andThen f)
    case GetDefaultPageSize(onResult) => GetDefaultPageSize(onResult andThen f)
  }

  def lift: Script[A] = liftF(this)
}

case class GenerateId[A](onResult: String => A) extends AppAction[A]
case class CurrentTime[A](onResult: OffsetDateTime => A) extends AppAction[A]
case class GetDefaultPageSize[A](onResult: Int => A) extends AppAction[A]

sealed trait EventStoreAction[A]
case class SaveEvent[A](event: Event, next: A) extends AppAction[A] with EventStoreAction[A]
case class ListEvents[A](entityId:Option[EntityId], pageSize:Int, pageNumber: Option[Long], onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class ListEventsByRange[A](id:EntityId, from: Option[OffsetDateTime], to:Option[OffsetDateTime], onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class SaveSnapshot[A](snapshot: Snapshot, next: A) extends AppAction[A] with EventStoreAction[A]
case class GetEventsCount[A](entityId: Option[EntityId], onResult:Long => A) extends AppAction[A] with EventStoreAction[A]


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

  def getDefaultPageSize: Script[Int] = GetDefaultPageSize(identity).lift
}


object EventStoreAction {
  def getEventsCount(entityId: Option[EntityId]):Script[Long] = GetEventsCount(entityId, identity).lift
  def saveEvent(event: Event): Script[Unit] = SaveEvent(event, ()).lift
  def listEvents(entityId: Option[EntityId], pageSize:Int, pageNumber:Option[Long]): Script[List[Event]] = ListEvents(entityId, pageSize, pageNumber, identity).lift
  def listEventsByRange(id: EntityId, from: MaybeTime = None, to: MaybeTime = None): Script[List[Event]] =
    ListEventsByRange(id, from, to, identity).lift

  def saveSnapshot(snapshot: Snapshot): Script[Unit] = SaveSnapshot(snapshot, ()).lift
}

