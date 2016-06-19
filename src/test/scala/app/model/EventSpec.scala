package app.model

import _root_.io.circe.Json
import org.specs2.ScalaCheck
import org.scalacheck.Arbitrary._
import org.specs2._
//import org.specs2.specification._
//import org.specs2.matcher._
import app.model.ModelGenerators._

class EventSpec extends Specification with ScalaCheck {

  def is = s2"""
  ReplayEvents
    the starting empty event will be ignored            $startWithEmpty
    ending with empty events will be empty              $endWithEmpty
  """

  def startWithEmpty = prop((events: List[Json]) =>  {
    Event.replayJsonEvents(Json.Null :: events) === Event.replayJsonEvents(events)
  })

  def endWithEmpty = prop((events: List[Json]) =>  {
    Event.replayJsonEvents(events ::: List(Json.Null)) === Json.Null
  })

}
