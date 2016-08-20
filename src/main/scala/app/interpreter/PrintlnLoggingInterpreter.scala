package app.interpreter

import app.logging.{SavedEvent, SavedSnapshot, GetEntityLog, AppLog}

object PrintlnLoggingInterpreter extends LoggingInterpreter {
  override def log(log: AppLog): Unit = log match {
    case GetEntityLog(time, snapshot, events, entity) =>
      println(
        s"Request to generate entity: $time: Snapshot $snapshot Number of events: ${events.size} Generated entity: $entity"
      )
    case SavedSnapshot(snapshot) => println(s"Saved snapshot: $snapshot")
    case SavedEvent(event)       => println(s"Saved event: $event")
  }
}
