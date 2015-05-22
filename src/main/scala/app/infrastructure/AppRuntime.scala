package app.infrastructure

import app.Routes.AppRoutes
import app.interpreter.AppInterpreter
import unfiltered.netty.future.Plan.Intent
import unfiltered.response.{HttpResponse, ResponseFunction}

import scala.concurrent.ExecutionContext

object AppRuntime {

  def frameworkifyRoutes(appRoutes: AppRoutes, interpreter: AppInterpreter)(implicit ec:ExecutionContext): Intent = {
    case req if appRoutes.isDefinedAt(req) => interpreter.run(appRoutes(req))

  }

}
