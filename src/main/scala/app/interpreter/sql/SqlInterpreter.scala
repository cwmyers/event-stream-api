package app.interpreter.sql

import java.sql.Timestamp

import app.action._
import app.interpreter.EventStoreInterpreter
import app.model.{SystemName, EntityId}
import cats.Functor
import cats.std.all._
import cats.syntax.all._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.slick.driver.PostgresDriver.simple._

class SqlInterpreter(db: SlickDatabase)(implicit ec: ExecutionContext)
    extends EventStoreInterpreter {
  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event) =>
      Future {
        db.withSession { implicit session =>
          EventsTable.events += eventToFields(event)
        }
        ()
      }
    case GetEventsCount(entityId, systemName) =>
      Future.successful {
        db.withSession { implicit session =>
          val q = for {
            events <- EventsTable.events
          } yield events

          filterEntityAndSystemName(entityId, systemName)(q).length.run
        }
      }
    case GetLatestSnapshot(entityId, systemName, time) =>
      Future.successful {
        db.withSession { implicit session =>
          SnapshotsTable.snapshots
            .filter(_.entityId === entityId.toString)
            .filter(_.systemName === systemName.toString)
            .filter(_.timestamp <= Timestamp.from(time.toInstant))
            .sortBy(_.timestamp.desc)
            .take(1)
            .list
            .headOption
            .map((createSnapshot _).tupled)

        }
      }
    case ListEvents(entityId, systemName, pageSize, pageNumber) =>
      Future.successful {
        db.withSession { implicit session =>
          val query = EventsTable.events.drop(pageNumber.orEmpty).take(pageSize)

          convert(filterEntityAndSystemName(entityId, systemName)(query).list)
        }
      }
    case ListEventsByRange(entityId, systemName, from, to) =>
      Future.successful {
        db.withSession { implicit session =>
          val q = EventsTable.events
            .filter(_.entityId === entityId.toString)
            .filter(_.systemName === systemName.toString)
            .filter(_.suppliedTimetamp <= fromOffsetDateTime(to))
          val q1 = from.fold(q)(f => q.filter(_.suppliedTimetamp >= fromOffsetDateTime(f)))
          convert(q1.list)
        }
      }
    case SaveSnapshot(snapshot) =>
      Future.successful {
        db.withSession { implicit session =>
          SnapshotsTable.snapshots += snapshotToFields(snapshot)
        }
        ()
      }
  }

  def filterEntityAndSystemName(entityId: Option[EntityId], systemName: Option[SystemName]) = {
    filterEntityId(entityId) _ andThen filterSystemName(systemName)
  }

  def convert[F[_]: Functor](events: F[EventsTable.Fields]) = events.map((createEvent _).tupled)

  private def filterEntityId(
    id: Option[EntityId]
  )(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    id.fold(q)(id => q.filter(_.entityId === id.toString))

  private def filterSystemName(
    systemName: Option[SystemName]
  )(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    systemName.fold(q)(name => q.filter(_.systemName === name.toString))

  def createDDL() = {
    val ddl = EventsTable.events.ddl ++ SnapshotsTable.snapshots.ddl
    db.withSession { implicit session =>
      ddl.create
    }
  }

}
