package app.util

import app.model.{Links, URI, Wrap}

import Wrap.ops._

import scala.language.higherKinds
import cats.Foldable
import cats.syntax.all._
import cats.std.all._


object LinksMaker {
  def createLinks[F[_]: Foldable, U:Wrap](id: F[U], currentPage: Long, lastPage: Long, pageSize: Int): Links = {
    val link = LinksMaker.makeLink("entity", id, pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage + 1).some
    val prevPage = if (currentPage == 0) None else link(currentPage - 1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  def makeLink[F[_] : Foldable, U: Wrap](endpoint: String, id: F[U], pageSize: Int)(pageNumber: Long) = {
    val entity = id.foldMap { i =>
      i.unwrap
    }
    URI(s"/$endpoint/$entity?pageNumber=$pageNumber&pageSize=$pageSize")
  }
}
