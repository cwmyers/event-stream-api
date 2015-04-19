package app

import java.time.OffsetDateTime

import app.model.EventId

package object interpreter {

  type TimeInterpreter = () => OffsetDateTime
  type IdGeneratorInterpreter = () => String
  type ConfigInterpreter = () => Int

}
