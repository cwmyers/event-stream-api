package app.model

import java.time.OffsetDateTime

import cats.data.Xor
import cats.std.all._
import io.circe._
import wrap.WrapString
import wrap.auto._
//import io.circe.generic.auto._
import scala.util.Try

object Codecs {

  implicit def DateDecoder: Decoder[OffsetDateTime] =
    Decoder[String].emap(dateString =>
      Xor.fromTry(Try(OffsetDateTime.parse(dateString)))
        .leftMap(_ => "Unable to parse date, it must be in ISO8601 format"))

  implicit def DateEncoder: Encoder[OffsetDateTime] = Encoder.instance(d => Json.fromString(d.toString))

  //  implicit def WrapperCodec[A:WrapString]: CodecJson[A] =
  //    CodecJson.derived(
  //      EncodeJson(e => jString(implicitly[WrapString[A]].unwrap(e))),
  //      DecodeJson(c => c.as[String](StringDecodeJson) map (s => implicitly[WrapString[A]].wrap(s))))

  implicit def WrapStringDecoder[A: WrapString]: Decoder[A] = Decoder[String].map(implicitly[WrapString[A]].wrap)

  implicit def WrapStringEncoder[A: WrapString]: Encoder[A] = Encoder.instance(a => Json.fromString(implicitly[WrapString[A]].unwrap(a)))

  implicit def ReceivedEventEncoder: Encoder[ReceivedEvent] =
    Encoder.forProduct4("entityId", "systemName", "timestamp", "body")(ReceivedEvent.unapply _ andThen (_.get))

  implicit def ReceivedEventDecoder: Decoder[ReceivedEvent] =
    Decoder.forProduct4("entityId", "systemName", "timestamp", "body")(ReceivedEvent)

  implicit def EventDecoder: Decoder[Event] = Decoder.forProduct6("id", "entityId",
    "systemName", "createdTimestamp", "suppliedTimestamp", "body")(Event.apply)

  implicit def EventEncoder: Encoder[Event] = Encoder.forProduct6("id", "entityId",
    "systemName", "createdTimestamp", "suppliedTimestamp", "body")(Event.unapply _ andThen (_.get))

  implicit def SnapshotEncoder: Encoder[Snapshot] = Encoder.forProduct5("id", "entityId",
    "systemName", "timestamp", "body")(Snapshot.unapply _ andThen (_.get))

  implicit def LinksEncoder: Encoder[Links] =
    Encoder.forProduct4("self", "first", "next", "prev")(Links.unapply _ andThen (_.get))

  implicit def LinkedResponseEncoder: Encoder[LinkedResponse] =
    Encoder.forProduct4("events", "pageNumber", "pageSize", "_links")(LinkedResponse.unapply _ andThen (_.get))

  implicit def StateEncoder: Encoder[State] = Encoder.forProduct2("systemName", "body")(State.unapply _ andThen (_.get))

  implicit def EntityCodec: Encoder[Entity] = Encoder.forProduct2("entityId", "state")(Entity.unapply _ andThen (_.get))


  //
  //
  //  implicit def ReceivedEventCodec: CodecJson[ReceivedEvent] =
  //    casecodec4(ReceivedEvent.apply, ReceivedEvent.unapply)("entityId", "systemName", "timestamp", "body")
  //
  //  implicit def EventCodec: CodecJson[Event] =
  //    casecodec6(Event.apply, Event.unapply)("id", "entityId",
  //      "systemName", "createdTimestamp", "suppliedTimestamp", "body")
  //
  //  implicit def SnapshotEncoder: EncodeJson[Snapshot] =
  //    jencode5L(Snapshot.unapply _ andThen (_.get))("id", "entityId", "systemName", "timestamp", "body")
  //
  //  implicit def LinksEncoder: EncodeJson[Links] =
  //    jencode4L(Links.unapply _ andThen (_.get))("self", "first", "next", "prev")
  //
  //  implicit def LinkedResponseEncoder: EncodeJson[LinkedResponse] =
  //    jencode4L(LinkedResponse.unapply _ andThen (_.get))("events", "pageNumber", "pageSize", "_links")
  //
  //  implicit def StateEncoder: EncodeJson[State] = jencode2L(State.unapply _ andThen (_.get))("systemName", "body")
  //
  //  implicit def EntityCodec: EncodeJson[Entity] = jencode2L(Entity.unapply _ andThen (_.get))("entityId", "state")


}
