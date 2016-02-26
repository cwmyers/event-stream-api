package app.util

import app.model.{Links, URI}
import wrap.WrapString

import scala.language.higherKinds
import cats.Foldable
import cats.syntax.all._
import cats.std.all._


object LinksMaker {
  def createLinks[F[_]: Foldable, U:WrapString](endpointName:String,id: F[U], currentPage: Long, lastPage: Long, pageSize: Int): Links = {
    val link = makeLink(endpointName, id, pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage + 1).some
    val prevPage = if (currentPage == 0) None else link(currentPage - 1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  def makeLink[F[_] : Foldable, U](endpoint: String, id: F[U], pageSize: Int)(pageNumber: Long)(implicit wrap:WrapString[U]) = {
    val entity = id.foldMap { i =>
      wrap.unwrap(i)
    }
    URI(s"/$endpoint/$entity?pageNumber=$pageNumber&pageSize=$pageSize")
  }
}
