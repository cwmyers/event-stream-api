package app.action

import java.time.OffsetDateTime

import app.MaybeTime
import app.action.AppAction.Script
import app.infrastructure.Config
import app.logging.AppLog
import app.model._
import cats.Monad
import cats.free.Free.liftF
import cats.free._

sealed trait AppAction[A] {
  def lift: Script[A] = liftF(this)
}

case object GenerateId extends AppAction[String]
case object CurrentTime extends AppAction[OffsetDateTime]
case object GetConfig extends AppAction[Config]
case class LogAction(log: AppLog) extends AppAction[Unit]

sealed trait EventStoreAction[A]
case class SaveEvent(event: Event) extends AppAction[Unit] with EventStoreAction[Unit]
case class ListEvents(entityId:Option[EntityId], systemName:Option[SystemName], pageSize:Int, pageNumber: Option[Long]) extends AppAction[List[Event]] with EventStoreAction[List[Event]]
case class ListEventsByRange(id:EntityId, systemName: SystemName, from: Option[OffsetDateTime], to:OffsetDateTime) extends AppAction[List[Event]] with EventStoreAction[List[Event]]
case class SaveSnapshot(snapshot: Snapshot) extends AppAction[Unit] with EventStoreAction[Unit]
case class GetEventsCount(entityId: Option[EntityId], systemName:Option[SystemName]) extends AppAction[Long] with EventStoreAction[Long]

case class GetLatestSnapshot(entityId: EntityId, systemName: SystemName, time: OffsetDateTime) extends AppAction[Option[Snapshot]] with EventStoreAction[Option[Snapshot]]


object AppAction {
  type Script[A] = Free[AppAction, A]

  def noAction[A](a: A): Script[A] = Monad[Script].pure(a)
  def generateId: Script[String] = GenerateId.lift
  def generateEventId: Script[EventId] = generateId map EventId
  def generateSnapshotId: Script[SnapshotId] = generateId map SnapshotId

  def currentTime: Script[OffsetDateTime] = CurrentTime.lift

  def getConfig: Script[Config] = GetConfig.lift

  def log(appLog: AppLog): Script[Unit] = LogAction(appLog).lift
}


object EventStoreAction {
  def getEventsCount(entityId: Option[EntityId], systemName:Option[SystemName]):Script[Long] =
    GetEventsCount(entityId, systemName).lift
  def saveEvent(event: Event): Script[Unit] = SaveEvent(event).lift
  def listEvents(entityId: Option[EntityId], systemName:Option[SystemName], pageSize:Int,
                 pageNumber:Option[Long]): Script[List[Event]] = ListEvents(entityId, systemName, pageSize, pageNumber).lift
  def listEventsByRange(id: EntityId, systemName: SystemName, from: MaybeTime, to: OffsetDateTime): Script[List[Event]] =
    ListEventsByRange(id, systemName, from, to).lift

  def saveSnapshot(snapshot: Snapshot): Script[Unit] = SaveSnapshot(snapshot).lift

  def getLatestSnapshotBefore(id: EntityId, systemName: SystemName, time: OffsetDateTime): Script[Option[Snapshot]] =
    GetLatestSnapshot(id, systemName, time).lift
}

