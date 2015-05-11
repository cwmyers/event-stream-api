package app.model

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.Date

import argonaut._, Argonaut._
import org.scalacheck.{Arbitrary, Gen}
import scalaz._, Scalaz._

object ModelGenerators {

  val genId = Gen.listOf(Gen.alphaNumChar).map(_.mkString)
  val genSnapshotId = genId map SnapshotId
  val genEventId = genId map EventId
  val genEntityId = genId map EntityId
  val genSystemName = Gen.alphaStr map SystemName

  val genTimestamp = Gen.posNum[Long] map (l => OffsetDateTime.ofInstant(new Date(l).toInstant, ZoneOffset.UTC))


  def genSnapshot = for {
    snapshotId <- genSnapshotId
    entityId <- genEntityId
    systemName <- genSystemName
    timestamp <- genTimestamp
  } yield Snapshot(snapshotId, entityId, systemName, timestamp, Json())


  private def minTime(t1: OffsetDateTime, t2: OffsetDateTime): OffsetDateTime =
    if (t1.isBefore(t2)) t1 else t2

  private def maxTime(t1: OffsetDateTime, t2: OffsetDateTime): OffsetDateTime =
    if (t1.isAfter(t2)) t1 else t2

  def genEvent = for {
    eventId <- genEventId
    entityId <- genEntityId
    systemName <- genSystemName
    time1 <- genTimestamp
    time2 <- genTimestamp
    json <- genJson
  } yield Event(eventId, entityId, systemName, maxTime(time1, time2), minTime(time1, time2), json)

  def genJString = Gen.alphaStr map jString

  def genJObject:Gen[Json] = for {
    str <- Gen.alphaStr.suchThat(!_.isEmpty)
    json <- genJson
  } yield Json.obj(str -> json)

  def genJArray: Gen[Json] =
    Gen.listOf(genJString) map jArrayElements


    def genJNumber:Gen[Json] = for {
      d <- Arbitrary.arbDouble.arbitrary
    } yield jNumberOrNull(d)

  
  
  
  def genJson: Gen[Json] = Gen.oneOf(genJString, genJObject, genJArray, genJNumber)

  implicit def arbJson: Arbitrary[Json] = Arbitrary(genJson)

  implicit def arbEvent: Arbitrary[Event] = Arbitrary(genEvent)


}
