package app.model

import java.time.OffsetDateTime

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson, CodecJson}

import scala.util.Try

object Codecs {

  implicit def DateCodec: CodecJson[OffsetDateTime] = CodecJson.derived(
    EncodeJson[OffsetDateTime](d => jString(d.toString)),
    DecodeJson.optionDecoder(_.string flatMap (dateString => Try(OffsetDateTime.parse(dateString)).toOption),
      "Unable to parse date, it must be in ISO8601 format")
  )

  implicit def EventIdCodec: CodecJson[EventId] = WrapperCodec(EventId, _.id)
  implicit def EntityIdCodec: CodecJson[EntityId] = WrapperCodec(EntityId, _.id)

  implicit def WrapperCodec[A](decode: String => A, encode: A => String): CodecJson[A] =
    CodecJson.derived(
      EncodeJson(e => jString(encode(e))),
      DecodeJson(c => c.as[String] map decode))


  implicit def ReceivedEventCodec: CodecJson[ReceivedEvent] =
    casecodec3(ReceivedEvent.apply, ReceivedEvent.unapply)("entityId", "timestamp", "body")

  implicit def EventCodec: CodecJson[Event] =
    casecodec5(Event.apply, Event.unapply)("id", "entityId", "createdTimestamp", "suppliedTimestamp", "body")


}
