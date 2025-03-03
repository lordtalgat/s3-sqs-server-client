package talgat.home

import cats.effect.{ExitCode, IO, IOApp}

object MainClient extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
   S3Downloader.getList().as(ExitCode.Success)
  }
}