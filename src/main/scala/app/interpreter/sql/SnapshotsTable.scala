package app.interpreter.sql

import java.sql.Timestamp

//import MyPostgresDriver.api._
import slick.driver.PostgresDriver.api._

object SnapshotsTable {
  type Fields = (String, String, String, Timestamp, String)
  val snapshots = TableQuery[SnapshotsTable]
}

class SnapshotsTable(tag: Tag) extends Table[SnapshotsTable.Fields](tag, "snapshots") {
  def id = column[String]("id", O.PrimaryKey)

  def entityId = column[String]("entity_id")

  def systemName = column[String]("system_name")

  def timestamp = column[Timestamp]("created_timestamp")

  def body = column[String]("body")

  def * = (id, entityId, systemName, timestamp, body)

}
