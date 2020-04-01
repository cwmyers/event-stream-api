package app.infrastructure

import app.Routes.AppRoutes
import app.interpreter.AppInterpreter
import unfiltered.netty.future.Plan.Intent

object AppRuntime {

  def frameworkifyRoutes(appRoutes: AppRoutes,
                         interpreter: AppInterpreter): Intent = {
    case req if appRoutes.isDefinedAt(req) => interpreter.run(appRoutes(req))

  }

}
