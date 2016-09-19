package app.interpreter.sql

import com.github.tminglei.slickpg._
import slick.driver.JdbcProfile
import slick.profile.Capability

trait MyPostgresDriver extends ExPostgresDriver with PgJsonSupport {
  def pgjson = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with JsonImplicits {}
}

object MyPostgresDriver extends MyPostgresDriver
