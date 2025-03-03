package talgat.home.models

import io.circe.parser._
import io.circe.generic.auto._

case class Event(action: String,
                 urls: List[String])

object Event {
  def fromString(jsonString: String): Option[Event] = {
    val json = parse(jsonString)
    val event: Either[io.circe.Error, Event] = json.flatMap(_.as[Event])

    event match {
      case Right(value) => Some(value)
      case _ => None
    }
  }
}
