package app.model

import java.time.OffsetDateTime

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson, CodecJson}

import scala.util.Try

object Codecs {

  implicit def DateCodec: CodecJson[OffsetDateTime] = CodecJson.derived(
    EncodeJson[OffsetDateTime](d => jString(d.toString)),
    DecodeJson.optionDecoder(_.string flatMap(dateString => Try(OffsetDateTime.parse(dateString)).toOption),
      "Unable to parse date, it must be in ISO8601 format")
  )

  implicit def EventCodec:CodecJson[Event] = casecodec2(Event.apply, Event.unapply)("timestamp","body")


}
