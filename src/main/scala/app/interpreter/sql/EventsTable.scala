package app.interpreter.sql

import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._

object EventsTable {
  type Fields = (String, String, String, Timestamp, Timestamp, String)
  val events = TableQuery[EventsTable]
}

class EventsTable(tag: Tag) extends Table[EventsTable.Fields](tag, "events") {
  def id = column[String]("id", O.PrimaryKey)

  def entityId = column[String]("entity_id")

  def systemName = column[String]("system_name")

  def createdTimestamp = column[Timestamp]("created_timestamp")

  def suppliedTimetamp = column[Timestamp]("supplied_timestamp")

  def body = column[String]("body")

  def * = (id, entityId, systemName, createdTimestamp, suppliedTimetamp, body)

}
