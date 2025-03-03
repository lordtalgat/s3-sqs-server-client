package talgat.home.processors

import talgat.home.{FileIO, S3VideoUploader, SQSService}
import talgat.home.models.Event

object EventProcessor {
  def process(eventOpt: Option[Event]): String = {
    eventOpt.fold("could not parse json") {
      case Event(action, urls) =>
        action match {
          case "url" => processUrl(urls)
          case "sqs-create" => processSqs(urls)
          case "sqs-run" => runSqs()
          case _ => "event could not be process"
        }
    }
  }

  private def processUrl(urls: List[String]): String = {
    urls.foreach { url =>
      FileIO.download(url).foreach { fileName =>
        val process =  for {
          _ <- S3VideoUploader.uploadFile(fileName)
          _ <- FileIO.delete(fileName)
        } yield ()
        process.unsafeRunSync()
      }
    }
    "urls processed"
  }

  private def processSqs(urls: List[String]): String = {
    urls.foreach { url =>
      SQSService.produceMessage(url).unsafeRunSync()
    }
    "sqs urls processed"
  }

  private def runSqs(): String = {
    SQSService.consumeMessage().unsafeRunSync()
    "sqs run processed"
  }
}