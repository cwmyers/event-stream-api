# event-stream-api

Demo for Melbourne Scala User Group.

## Quick Start

```
    ./sbt run
```

and to bootstrap with some data:

```
    cd /src/test/resources
    ./play.sh
```

## Actions

This is a system that store deltas or events that can be replayed to represent the state of an entity.  It has a 
REST like interface to save, retrieve and snapshot events.
 
Events can be streamed for downstream clients wishing to consumer events.

Here are the current actions:

 - `POST /event` with an event body 
 - `GET /events` --> a paginated stream of events (optional params `?pageSize=10&pageNumber=1&systemName=<systemName>`)
 - `GET /events/<enitiyId>` --> a paginated stream of events for the given entity Id (optional params `?pageSize=10&pageNumber=1&systemName=<systemName>`)
 - `GET /entity<entityId>/<systemName>` --> replays the events building up the entity (optionally pass through the point in time `?at=2015-05-25T12:00:00Z`)
 - `POST /snapshot/<entityId>/<systemName>/<time>` --> Creates a snapshot for the entity at the supplied time.
 
 
## Database setup

You can use the in memory mutable map by default or you can configure a postgresql database by uncommenting the following line in
`Main.scala`:


```
  //private val db = new SlickDatabase("events", "events",
  //  "jdbc:postgresql://localhost/events", "org.postgresql.Driver")

  //private val eventStoreInterpreter = new SqlInterpreter(db)
  //Try(sqlInterpreter.createDDL())
```

and comment out:
```
    private val eventStoreInterpreter = new MutableMapEventStoreInterpreter()
```

You will need a running postgresql server with a database called `events` and a user called `events` with password
`events`.