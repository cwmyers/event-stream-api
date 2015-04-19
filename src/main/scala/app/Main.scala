package app

import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.Executors

import app.infrastructure.AppRuntime.frameworkifyRoutes
import app.infrastructure.{Config, AppServer, NoRoute}
import app.interpreter.{DispatchInterpreter, IdGeneratorInterpreter, TestEventStoreInterpreter, TimeInterpreter}
import unfiltered.netty.Server
import unfiltered.netty.future.Plan.Intent
import unfiltered.netty.future.Planify

import scala.concurrent.ExecutionContext

object Main extends AppServer {
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(40))
  val port = 9090

  private val idGenerator: IdGeneratorInterpreter =
    () => UUID.randomUUID().toString

  private val timeGenerator: TimeInterpreter = () => OffsetDateTime.now()

  private val configInterpreter = () => Config(defaultPageSize = 10)

  private val interpreter = new DispatchInterpreter(new TestEventStoreInterpreter,
    idGenerator, timeGenerator, configInterpreter)


  val appRoutes = frameworkifyRoutes(Routes.appRoutes, interpreter)

  val routes: Intent = appRoutes orElse NoRoute()

  override def server: Server = unfiltered.netty.Server.http(port).makePlan(Planify(routes))

  def main(args: Array[String]): Unit = {
    server.start()
  }


}
