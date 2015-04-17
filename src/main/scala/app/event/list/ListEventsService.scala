package app.event.list

import app.action.AppAction.Script
import app.action.EventStoreAction
import app.model.Event
import scalaz._, Scalaz._

object ListEventsService {
  case class URI(url:String) extends AnyVal

  case class Links(selfPageUrl: URI, firstPageUrl: URI,
                   nextPageUrl: Option[URI], previousPageUrl: Option[URI])

  case class LinkedResponse(events: List[Event], pageNumber: Option[Long], pageSize: Int, links: Links)

  def getEvents(pageSize: Int, pageNumber: Option[Long]): Script[LinkedResponse] = for {
    events <- EventStoreAction.listEvents(pageSize, pageNumber)
    totalCount <- EventStoreAction.getEventsCount
    lastPage = Math.max((totalCount/pageSize)-1,0)
  } yield LinkedResponse(events, pageNumber, pageSize, createLinks(pageNumber.getOrElse(lastPage), lastPage, pageSize))

  private def createLinks(currentPage: Long, lastPage: Long, pageSize: Int): Links = {
    val link = makeLink(pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage+1).some
    val prevPage = if (currentPage == 0) None else link(currentPage-1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  private def makeLink(pageSize: Int)(pageNumber: Long) = URI(s"/events?pageNumber=$pageNumber&pageSize=$pageSize")

}
