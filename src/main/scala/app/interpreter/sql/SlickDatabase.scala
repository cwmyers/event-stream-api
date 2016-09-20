package app.interpreter.sql

import slick.jdbc.JdbcBackend

/**
  * Wrapper around a Slick DatabaseDef that remembers the connection parameters.
  */
class SlickDatabase(val username: String,
                    val password: String,
                    val url: String,
                    val driver: String) {

  lazy val database = {
    JdbcBackend.Database.forURL(url, user = username, password = password, driver = driver)
  }

}
