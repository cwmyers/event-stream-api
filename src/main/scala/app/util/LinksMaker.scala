package app.util

import app.model.{EntityId, Links, SystemName, URI}
import cats.Foldable
import cats.instances.all._
import cats.syntax.all._

object LinksMaker {
  def createLinks[F[_]: Foldable](endpointName: String,
                                  id: F[EntityId],
                                  systemName: F[SystemName],
                                  currentPage: Long,
                                  lastPage: Long,
                                  pageSize: Int): Links = {
    val link     = makeLink(endpointName, id, systemName, pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage + 1).some
    val prevPage = if (currentPage == 0) None else link(currentPage - 1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  def makeLink[F[_]: Foldable](endpoint: String,
                               id: F[EntityId],
                               systemName: F[SystemName],
                               pageSize: Int)(pageNumber: Long) = {
    val entity = id.foldMap { i =>
      i.toString
    }
    val system = systemName.foldMap { s =>
      s"&systemName=$s"
    }
    URI(s"/$endpoint/$entity?pageNumber=$pageNumber&pageSize=$pageSize$system")
  }
}
