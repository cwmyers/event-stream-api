package app

import java.time.OffsetDateTime

import app.infrastructure.Config

package object interpreter {

  type TimeInterpreter = () => OffsetDateTime
  type IdGeneratorInterpreter = () => String
  type ConfigInterpreter = () => Config

}
