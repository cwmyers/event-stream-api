package app.interpreter.sql

import java.sql.Timestamp

import MyPostgresDriver.api._
import com.github.tminglei.slickpg.JsonString

object EventsTable {
  type Fields = (String, String, String, Timestamp, Timestamp, JsonString)
  val events = TableQuery[EventsTable]
}

class EventsTable(tag: Tag) extends Table[EventsTable.Fields](tag, "events") {
  def id = column[String]("id", O.PrimaryKey)

  def entityId = column[String]("entity_id")

  def systemName = column[String]("system_name")

  def createdTimestamp = column[Timestamp]("created_timestamp")

  def suppliedTimestamp = column[Timestamp]("supplied_timestamp")

  def body = column[JsonString]("body", O.SqlType("jsonb"))

  def * = (id, entityId, systemName, createdTimestamp, suppliedTimestamp, body)

}
