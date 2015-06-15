package app.action

import java.time.OffsetDateTime

import app.MaybeTime
import app.action.AppAction.Script
import app.infrastructure.Config
import app.logging.AppLog
import app.model._

import scalaz.Free._
import scalaz._

sealed trait AppAction[A] {
  def lift: Script[A] = liftFC(this)
}

case class GenerateId[A](onResult: String => A) extends AppAction[A]
case class CurrentTime[A](onResult: OffsetDateTime => A) extends AppAction[A]
case class GetConfig[A](onResult: Config => A) extends AppAction[A]
case class LogAction[A](log: AppLog, next:A) extends AppAction[A]

sealed trait EventStoreAction[A]
case class SaveEvent[A](event: Event, next: A) extends AppAction[A] with EventStoreAction[A]
case class ListEvents[A](entityId:Option[EntityId], systemName:Option[SystemName],pageSize:Int, pageNumber: Option[Long], onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class ListEventsByRange[A](id:EntityId, systemName: SystemName, from: Option[OffsetDateTime], to:OffsetDateTime, onResult: List[Event] => A) extends AppAction[A] with EventStoreAction[A]
case class SaveSnapshot[A](snapshot: Snapshot, next: A) extends AppAction[A] with EventStoreAction[A]
case class GetEventsCount[A](entityId: Option[EntityId], systemName:Option[SystemName], onResult:Long => A) extends AppAction[A] with EventStoreAction[A]

case class GetLatestSnapshot[A](entityId: EntityId, systemName: SystemName, time: OffsetDateTime, onResult: Option[Snapshot] => A) extends AppAction[A] with EventStoreAction[A]


object AppAction {
  type Script[A] = FreeC[AppAction, A]

  implicit val MonadAppAction: Monad[Script] =
    Free.freeMonad[({type λ[α] = Coyoneda[AppAction, α]})#λ]


  def noAction[A](a: A): Script[A] = Monad[Script].pure(a)
  def generateId: Script[String] = GenerateId(identity).lift
  def generateEventId: Script[EventId] = generateId map EventId
  def generateSnapshotId: Script[SnapshotId] = generateId map SnapshotId

  def currentTime: Script[OffsetDateTime] = CurrentTime(identity).lift

  def getConfig: Script[Config] = GetConfig(identity).lift

  def log(appLog: AppLog): Script[Unit] = LogAction(appLog, ()).lift
}


object EventStoreAction {
  def getEventsCount(entityId: Option[EntityId], systemName:Option[SystemName]):Script[Long] =
    GetEventsCount(entityId, systemName, identity).lift
  def saveEvent(event: Event): Script[Unit] = SaveEvent(event, ()).lift
  def listEvents(entityId: Option[EntityId], systemName:Option[SystemName], pageSize:Int,
                 pageNumber:Option[Long]): Script[List[Event]] = ListEvents(entityId, systemName, pageSize, pageNumber, identity).lift
  def listEventsByRange(id: EntityId, systemName: SystemName, from: MaybeTime, to: OffsetDateTime): Script[List[Event]] =
    ListEventsByRange(id, systemName, from, to, identity).lift

  def saveSnapshot(snapshot: Snapshot): Script[Unit] = SaveSnapshot(snapshot, ()).lift

  def getLatestSnapshotBefore(id: EntityId, systemName: SystemName, time: OffsetDateTime): Script[Option[Snapshot]] =
    GetLatestSnapshot(id, systemName, time, identity).lift
}

