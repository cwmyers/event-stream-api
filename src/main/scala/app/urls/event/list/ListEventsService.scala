package app.urls.event.list

import app.action.AppAction.{Script, getConfig}
import app.action.EventStoreAction.{getEventsCount, listEvents}
import app.model._

import scalaz.Scalaz._

object ListEventsService {

  def getEvents(entityId: Option[EntityId], systemName: Option[SystemName],
                maybePageSize: Option[Int], pageNumber: Option[Long]): Script[LinkedResponse] = for {
    config <- getConfig
    pageSize = maybePageSize.getOrElse(config.defaultPageSize)
    events <- listEvents(entityId, systemName, pageSize, pageNumber)
    totalCount <- getEventsCount(entityId, systemName)
    lastPage = Math.max((totalCount / pageSize) - 1, 0)
  } yield LinkedResponse(events, pageNumber, pageSize, createLinks(entityId, pageNumber.getOrElse(lastPage), lastPage, pageSize))

  private def createLinks(entityId: Option[EntityId], currentPage: Long, lastPage: Long, pageSize: Int): Links = {
    val link = makeLink(entityId, pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage + 1).some
    val prevPage = if (currentPage == 0) None else link(currentPage - 1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  private def makeLink(entityId: Option[EntityId], pageSize: Int)(pageNumber: Long) = {
    val entity = ~entityId.map(id => s"/${id.id}")
    URI(s"/events$entity?pageNumber=$pageNumber&pageSize=$pageSize")
  }

}
