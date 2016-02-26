package app.model

import java.time.OffsetDateTime

import argonaut._, Argonaut._
import wrap.WrapString
import wrap.auto._
import cats.std.all._

import scala.util.Try

object Codecs {

  implicit def DateCodec: CodecJson[OffsetDateTime] = CodecJson.derived(
    EncodeJson[OffsetDateTime](d => jString(d.toString)),
    DecodeJson.optionDecoder(_.string flatMap (dateString => Try(OffsetDateTime.parse(dateString)).toOption),
      "Unable to parse date, it must be in ISO8601 format")
  )

  implicit def WrapperCodec[A:WrapString]: CodecJson[A] =
    CodecJson.derived(
      EncodeJson(e => jString(implicitly[WrapString[A]].unwrap(e))),
      DecodeJson(c => c.as[String](StringDecodeJson) map (s => implicitly[WrapString[A]].wrap(s))))


  implicit def ReceivedEventCodec: CodecJson[ReceivedEvent] =
    casecodec4(ReceivedEvent.apply, ReceivedEvent.unapply)("entityId", "systemName", "timestamp", "body")

  implicit def EventCodec: CodecJson[Event] =
    casecodec6(Event.apply, Event.unapply)("id", "entityId",
      "systemName", "createdTimestamp", "suppliedTimestamp", "body")

  implicit def SnapshotEncoder: EncodeJson[Snapshot] =
    jencode5L(Snapshot.unapply _ andThen (_.get))("id", "entityId", "systemName", "timestamp", "body")

  implicit def LinksEncoder: EncodeJson[Links] =
    jencode4L(Links.unapply _ andThen (_.get))("self", "first", "next", "prev")

  implicit def LinkedResponseEncoder: EncodeJson[LinkedResponse] =
    jencode4L(LinkedResponse.unapply _ andThen (_.get))("events", "pageNumber", "pageSize", "_links")

  implicit def StateEncoder: EncodeJson[State] = jencode2L(State.unapply _ andThen (_.get))("systemName", "body")

  implicit def EntityCodec: EncodeJson[Entity] = jencode2L(Entity.unapply _ andThen (_.get))("entityId", "state")


}
