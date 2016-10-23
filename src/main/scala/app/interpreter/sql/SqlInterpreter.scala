package app.interpreter.sql

import java.sql.Timestamp
import java.time.OffsetDateTime

import app.action._
import app.interpreter.EventStoreInterpreter
import app.model.{EntityId, SystemName}
import slick.dbio.Effect.Write

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.higherKinds
import MyPostgresDriver.api._
import app.MaybeTime
import cats.implicits._
import slick.dbio.{DBIOAction, Effect, NoStream}

import scala.concurrent.duration._

class SqlInterpreter(db: SlickDatabase)(implicit ec: ExecutionContext)
    extends EventStoreInterpreter {

  type EventQuery = Query[EventsTable, EventsTable.Fields, Seq]

  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event) =>
      executor[Int, NoStream, Write](EventsTable.events += eventToFields(event)).map(_ => ())
    case GetEventsCount(entityId, systemName, fromTime, toTime) =>
      db.database
        .run(
          filterOptionals(entityId, systemName, fromTime, toTime)(EventsTable.events).length.result
        )
        .map(_.toLong)
    case GetLatestSnapshot(entityId, systemName, time) =>
      val snaps = SnapshotsTable.snapshots
        .filter(_.entityId === entityId.toString)
        .filter(_.systemName === systemName.toString)
        .filter(_.timestamp <= Timestamp.from(time.toInstant))
        .sortBy(_.timestamp.desc)
        .take(1)
        .result
      db.database.run(snaps).map(_.headOption.map((createSnapshot _).tupled))

    case ListEvents(entityId, systemName, fromTime, toTime, pageSize, pageNumber) =>
      val query =
        filterOptionals(entityId, systemName, fromTime, toTime)(EventsTable.events)
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

  def filterOptionals(entityId: Option[EntityId],
                      systemName: Option[SystemName],
                      fromTime: MaybeTime,
                      toTime: MaybeTime) = {
    filterEntityId(entityId) andThen filterSystemName(systemName) andThen filterMaybe[
      OffsetDateTime
    ](fromTime, (t, e) => e.suppliedTimestamp >= Timestamp.from(t.toInstant)) andThen
      filterMaybe[OffsetDateTime](
        toTime,
        (t, e) => e.suppliedTimestamp <= Timestamp.from(t.toInstant)
      )
  }

  def convert[F[_]: cats.Functor](events: F[EventsTable.Fields]) =
    events.map((createEvent _).tupled)

  private def filterEntityId(id: Option[EntityId]): EventQuery => EventQuery = {
    filterMaybe[EntityId](id, (a, e) => e.entityId === a.toString)
  }

  private def filterSystemName(systemName: Option[SystemName]): EventQuery => EventQuery =
    filterMaybe[SystemName](systemName, (name, events) => events.systemName === name.toString)

  private def filterMaybe[A](maybe: Option[A], f: (A, EventsTable) => Rep[Boolean])(
    q: EventQuery
  ): EventQuery = maybe.fold(q)(a => q.filter(f(a, _)))

  def createDDL() = {
    val ddl = EventsTable.events.schema ++ SnapshotsTable.snapshots.schema
    Await.result(db.database.run(ddl.create), 10.seconds)
  }

  private def executor[R2, S2 <: NoStream, E2 <: Effect](a: DBIOAction[R2, S2, E2]) = {
    db.database.run(a.transactionally)
  }

}
