package app.model

import java.time.OffsetDateTime

import cats.data.Xor
import io.circe._

import scala.util.Try

object Codecs {
  private def stringEncoder[A <: String]:Encoder[A] = Encoder.instance(Json.fromString)

  implicit def DateDecoder: Decoder[OffsetDateTime] =
    Decoder[String].emap(dateString =>
      Xor.fromTry(Try(OffsetDateTime.parse(dateString)))
        .leftMap(_ => "Unable to parse date, it must be in ISO8601 format"))

  implicit def DateEncoder: Encoder[OffsetDateTime] = Encoder.instance(d => Json.fromString(d.toString))


  implicit def SystemNameDecoder: Decoder[SystemName] = Decoder[String].map(SystemName)
  implicit def SystemNameEncoder: Encoder[SystemName] = stringEncoder

  implicit def EventIdEncoder: Encoder[EventId] = stringEncoder
  implicit def EventIdDecoder: Decoder[EventId] = Decoder[String].map(EventId)

  implicit def EntityIdDecoder: Decoder[EntityId] = Decoder[String].map(EntityId)
  implicit def EntityIdEncoder: Encoder[EntityId] = stringEncoder

  implicit def SnapshotIdDecoder: Decoder[SnapshotId] = Decoder[String].map(SnapshotId)
  implicit def SnapshotIdEncoder: Encoder[SnapshotId] = stringEncoder

  implicit def URIDecoder: Decoder[URI] = Decoder[String].map(URI)
  implicit def URIEncoder: Encoder[URI] = stringEncoder

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

}
