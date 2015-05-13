package app.interpreter.sql


import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.JdbcBackend._

/**
 * Wrapper around a Slick DatabaseDef that remembers the connection parameters.
 */
class SlickDatabase(val username: String, val password: String, val url: String, val driver: String) {

  lazy val database = {
    JdbcBackend.Database.forURL(url,
      user = username,
      password = password,
      driver = driver
    )
  }

  def withSession[T](block: Session => T) = database withSession block

}


