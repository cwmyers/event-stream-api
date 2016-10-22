package app.interpreter.sql

import java.sql.Timestamp

import app.action._
import app.interpreter.EventStoreInterpreter
import app.model.{EntityId, SystemName}
import slick.dbio.Effect.Write

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.higherKinds
import MyPostgresDriver.api._
import cats.implicits._
import slick.dbio.{DBIOAction, Effect, NoStream}
import scala.concurrent.duration._

class SqlInterpreter(db: SlickDatabase)(implicit ec: ExecutionContext)
    extends EventStoreInterpreter {

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event) =>
      executor[Int, NoStream, Write](EventsTable.events += eventToFields(event)).map(_ => ())
    case GetEventsCount(entityId, systemName) =>
      db.database
        .run(filterEntityAndSystemName(entityId, systemName)(EventsTable.events).length.result)
        .map(_.toLong)
    case GetLatestSnapshot(entityId, systemName, time) =>
      val snaps = SnapshotsTable.snapshots
        .filter(_.entityId === entityId.toString)
        .filter(_.systemName === systemName.toString)
        .filter(_.timestamp <= Timestamp.from(time.toInstant))
        .sortBy(_.timestamp.desc)
        .take(1)
        .result
      val run1: Future[Seq[(String, String, String, Timestamp, String)]] = db.database.run(snaps)
      run1.map(_.headOption.map((createSnapshot _).tupled))

    case ListEvents(entityId, systemName, pageSize, pageNumber) =>
      val query = filterEntityAndSystemName(entityId, systemName)(EventsTable.events)
        .sortBy(_.suppliedTimestamp.desc)
        .drop(pageNumber.getOrElse(0L) * pageSize)
        .take(pageSize)

      executor(query.result).map(l => convert(l.toList))
    case ListEventsByRange(entityId, systemName, from, to) =>
      val q = EventsTable.events
        .filter(_.entityId === entityId.toString)
        .filter(_.systemName === systemName.toString)
        .filter(_.suppliedTimestamp <= fromOffsetDateTime(to))
      val q1 = from.fold(q)(f => q.filter(_.suppliedTimestamp >= fromOffsetDateTime(f)))
      executor(q1.result).map(l => convert(l.toList))
    case SaveSnapshot(snapshot) =>
      executor(SnapshotsTable.snapshots += snapshotToFields(snapshot)).map(_ => ())

  }

  def filterEntityAndSystemName(entityId: Option[EntityId], systemName: Option[SystemName]) = {
    filterEntityId(entityId) _ andThen filterSystemName(systemName)
  }

  def convert[F[_]: cats.Functor](events: F[EventsTable.Fields]) =
    events.map((createEvent _).tupled)

  private def filterEntityId(
    id: Option[EntityId]
  )(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    id.fold(q)(id => q.filter(_.entityId === id.toString))

  private def filterSystemName(
    systemName: Option[SystemName]
  )(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    systemName.fold(q)(name => q.filter(_.systemName === name.toString))

  def createDDL() = {
    val ddl = EventsTable.events.schema ++ SnapshotsTable.snapshots.schema
    Await.result(db.database.run(ddl.create), 10.seconds)
  }

  private def executor[R2, S2 <: NoStream, E2 <: Effect](a: DBIOAction[R2, S2, E2]) = {
    db.database.run(a.transactionally)
  }

}
