package app.urls.event.list

import app.MaybeTime
import app.action.AppAction.{Script, getConfig}
import app.action.EventStoreAction.{getEventsCount, listEvents}
import app.model._
import app.util.LinksMaker.createLinks
import cats.instances.all._

object ListEventsService {

  def getEvents(entityId: Option[EntityId],
                systemName: Option[SystemName],
                fromTime: MaybeTime,
                toTime: MaybeTime,
                maybePageSize: Option[Int],
                pageNumber: Option[Long]): Script[LinkedResponse] =
    for {
      config <- getConfig
      pageSize = maybePageSize.getOrElse(config.defaultPageSize)
      events <- listEvents(entityId, systemName, fromTime, toTime, pageSize, pageNumber)
      totalCount <- getEventsCount(entityId, systemName, fromTime, toTime)
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
