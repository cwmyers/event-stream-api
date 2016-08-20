package app.util

import app.model.{EntityId, Links, URI}
import cats.Foldable
import cats.std.all._
import cats.syntax.all._

import scala.language.higherKinds

object LinksMaker {
  def createLinks[F[_]: Foldable](endpointName: String,
                                  id: F[EntityId],
                                  currentPage: Long,
                                  lastPage: Long,
                                  pageSize: Int): Links = {
    val link     = makeLink(endpointName, id, pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage + 1).some
    val prevPage = if (currentPage == 0) None else link(currentPage - 1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  def makeLink[F[_]: Foldable](endpoint: String, id: F[EntityId], pageSize: Int)(
    pageNumber: Long
  ) = {
    val entity = id.foldMap { i =>
      i.toString
    }
    URI(s"/$endpoint/$entity?pageNumber=$pageNumber&pageSize=$pageSize")
  }
}
