package app

import java.util.concurrent.Executors

import _root_.infrastructure.AppRuntime.frameworkifyRoutes
import _root_.infrastructure.{AppServer, NoRoute}
import _root_.interpreter.{TestEventStoreInterpreter, DispatchInterpreter}
import unfiltered.netty.Server
import unfiltered.netty.future.Plan.Intent
import unfiltered.netty.future.Planify

import scala.concurrent.ExecutionContext

object Main extends AppServer {
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(40))
  val port = 9090

  val appRoutes = frameworkifyRoutes(Routes.appRoutes, new DispatchInterpreter(new TestEventStoreInterpreter))

  val routes: Intent = appRoutes orElse NoRoute()

  override def server: Server = unfiltered.netty.Server.http(port).makePlan(Planify(routes))

  def main(args: Array[String]) = {
    server.start()
  }


}
