package app

import java.time.OffsetDateTime

import app.infrastructure.Config
import app.logging.AppLog

package object interpreter {

  type TimeInterpreter = () => OffsetDateTime
  type IdGeneratorInterpreter = () => String
  type ConfigInterpreter = () => Config
  trait LoggingInterpreter {
    def log(appLog: AppLog): Unit
  }

}
