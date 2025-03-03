package talgat.home

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import talgat.home.models.Event
import talgat.home.processors.EventProcessor

object MainServer extends IOApp {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(s"Health is ok, dir is =${FileIO.dir}")
    case req@POST -> Root / "event" =>
      for {
        str <- req.as[String]
        eventOpt = Event.fromString(str)
        req <- Ok(EventProcessor.process(eventOpt))
      } yield req
  }

  // Main entry point for the HTTP server
  override def run(args: List[String]): IO[ExitCode] = {
    // Set up the server to listen on port 8080
    EmberServerBuilder
      .default[IO] // Use default configuration for Ember
      .withHttpApp(service.orNotFound) // Use our service as the HttpApp
      .withHost("0.0.0.0") // Bind to all available interfaces (localhost or external)
      .withPort(8080) // Set the port number
      .build // Build the server
      .use(_ => IO.never) // Start the server and block indefinitely
      .as(ExitCode.Success) // Exit code when server is shut down
  }
}