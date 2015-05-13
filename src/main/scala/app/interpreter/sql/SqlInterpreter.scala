package app.interpreter.sql

import java.sql.Timestamp

import app.action._
import app.interpreter.EventStoreInterpreter
import app.model.{EntityId, SystemName}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.slick.driver.PostgresDriver.simple._
import scalaz.Scalaz._
import scalaz._

class SqlInterpreter(db: SlickDatabase)(implicit ec: ExecutionContext) extends EventStoreInterpreter {
  override def run[A](eventStoreAction: EventStoreAction[A]): Future[A] = eventStoreAction match {
    case SaveEvent(event, next) => Future {
      db.withSession {
        implicit session =>
          EventsTable.events += eventToFields(event)
      }
      next
    }
    case GetEventsCount(entityId, systemName, onResult) => Future {
      db.withSession {
        implicit session =>
          val q = for {
            events <- EventsTable.events
          } yield events

          val result = filterEntityAndSystemName(entityId, systemName)(q).length.run
          onResult(result)
      }
    }
    case GetLatestSnapshot(entityId, systemName, time, onResult) => Future {
      db.withSession {
        implicit session =>
          onResult(SnapshotsTable.snapshots.filter(_.entityId === entityId.id)
            .filter(_.systemName === systemName.name)
            .filter(_.timestamp <= Timestamp.from(time.toInstant))
            .sortBy(_.timestamp.desc).take(1).list.headOption.map((createSnapshot _).tupled))

      }
    }
    case ListEvents(entityId, systemName, pageSize, pageNumber, onResult) => Future {
      db.withSession {
        implicit session =>
          val query = EventsTable.events.drop(~pageNumber).take(pageSize)

          onResult(convert(filterEntityAndSystemName(entityId, systemName)(query).list))
      }
    }
    case ListEventsByRange(entityId, systemName, from, to, onResult) => Future {
      db.withSession {
        implicit session =>
          val q = EventsTable.events
            .filter(_.entityId === entityId.id)
            .filter(_.systemName === systemName.name)
            .filter(_.suppliedTimetamp <= fromOffsetDateTime(to))
          val q1 = from.fold(q)(f => q.filter(_.suppliedTimetamp >= fromOffsetDateTime(f)))
          onResult(convert(q1.list))
      }
    }
    case SaveSnapshot(snapshot, next) => Future {
      db.withSession {
        implicit session =>
          SnapshotsTable.snapshots += snapshotToFields(snapshot)
      }
      next
    }
  }

  def filterEntityAndSystemName(entityId: Option[EntityId], systemName: Option[SystemName]) = {
    filterEntityId(entityId) _ andThen filterSystemName(systemName)
  }

  def convert[F[_] : Functor](events: F[EventsTable.Fields]) = events.map((createEvent _).tupled)


  private def filterEntityId(id: Option[EntityId])(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    id.fold(q)(id => q.filter(_.entityId === id.id))

  private def filterSystemName(systemName: Option[SystemName])(q: Query[EventsTable, EventsTable.Fields, Seq]): Query[EventsTable, EventsTable.Fields, Seq] =
    systemName.fold(q)(name => q.filter(_.systemName === name.name))

  def createDDL() = {
    val ddl = EventsTable.events.ddl ++ SnapshotsTable.snapshots.ddl
    db.withSession {
      implicit session =>
        ddl.create
    }
  }

}
