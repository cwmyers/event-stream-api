package app

import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.Executors

import app.infrastructure.AppRuntime.frameworkifyRoutes
import app.infrastructure.{Config, AppServer, NoRoute}
import app.interpreter._
import app.interpreter.sql.{SlickDatabase, SqlInterpreter}
import unfiltered.netty.Server
import unfiltered.netty.future.Plan.Intent
import unfiltered.netty.future.Planify

import scala.concurrent.ExecutionContext
import scala.util.Try

object Main extends AppServer {
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(40))
  val port = 9090

  private val idGenerator: IdGeneratorInterpreter =
    () => UUID.randomUUID().toString
  private val timeGenerator: TimeInterpreter = () => OffsetDateTime.now()
  private val configInterpreter = () => Config(defaultPageSize = 10)
  private val db = new SlickDatabase("events", "events",
    "jdbc:postgresql://localhost/events", "org.postgresql.Driver")

  private val sqlInterpreter = new SqlInterpreter(db)
  Try(sqlInterpreter.createDDL())
  private val interpreter = new DispatchInterpreter(sqlInterpreter,
    idGenerator, timeGenerator, configInterpreter, PrintlnLoggingInterpreter)

  val appRoutes = frameworkifyRoutes(Routes.appRoutes, interpreter)

  val routes: Intent = appRoutes orElse NoRoute()

  override def server: Server = unfiltered.netty.Server.http(port).makePlan(Planify(routes))

  def main(args: Array[String]): Unit = {
    server.start()
  }

}
