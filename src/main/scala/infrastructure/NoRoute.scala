package infrastructure

import unfiltered.netty.future.Plan.Intent
import unfiltered.request.Path
import unfiltered.response.{HtmlContent, NotFound, ResponseString}

import scala.concurrent.Future

object NoRoute {

  def apply(responseContent: String = "page not found"): Intent = {
    case Path(path) =>
      Future.successful(NotFound ~> HtmlContent ~> ResponseString(responseContent))
  }
}