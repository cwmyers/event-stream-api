package app.urls.event.list

import app.action.AppAction.{Script, getConfig}
import app.action.EventStoreAction.{getEventsCount, listEvents}
import app.model._
import app.util.LinksMaker.createLinks
import cats.instances.all._

object ListEventsService {

  def getEvents(entityId: Option[EntityId],
                systemName: Option[SystemName],
                maybePageSize: Option[Int],
                pageNumber: Option[Long]): Script[LinkedResponse] =
    for {
      config <- getConfig
      pageSize = maybePageSize.getOrElse(config.defaultPageSize)
      events <- listEvents(entityId, systemName, pageSize, pageNumber)
      totalCount <- getEventsCount(entityId, systemName)
      lastPage = Math.max((totalCount / pageSize) - 1, 0)
    } yield
      LinkedResponse(
        events,
        pageNumber,
        pageSize,
        createLinks(
          "events",
          entityId,
          systemName,
          pageNumber.getOrElse(lastPage),
          lastPage,
          pageSize
        )
      )

}
