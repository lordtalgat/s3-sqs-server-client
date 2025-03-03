package talgat.home

import cats.effect.IO

import java.io.{File, FileOutputStream, InputStream}
import java.net.URL

object FileIO {
  val dir: String = "/tmp/"

  def download(url: String): Option[String] = {
    try {
      val fileName: String = java.util.UUID.randomUUID.toString + ".mp4"
      val videoUrl = new URL(url)
      val inputStream: InputStream = videoUrl.openStream()
      saveInputStreamToFile(inputStream, dir + fileName)
      println(s"file $fileName downloaded")
      Some(fileName)
    } catch {
      case e: Exception =>
        println("video could not be downloaded")
        None
    }
  }

  def saveInputStreamToFile(inputStream: InputStream, filePath: String): Unit = {
    val outputStream = new FileOutputStream(new File(filePath))
    try {
      val buffer = new Array[Byte](1024)
      var bytesRead = inputStream.read(buffer)
      while (bytesRead != -1) {
        outputStream.write(buffer, 0, bytesRead)
        bytesRead = inputStream.read(buffer)
      }
    } finally {
      outputStream.close()
      inputStream.close()
    }
  }

  def delete(fileName: String): IO[Unit] = IO {
    val file = new File(dir + fileName)
    if (file.exists()) {
      file.delete()
      println(s"File '$fileName' deleted successfully.")
    } else {
      println(s"File '$fileName' does not exist.")
    }
  }

}
