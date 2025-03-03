package talgat.home

import cats.effect.IO
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{DeleteObjectRequest, GetObjectRequest, ListObjectsV2Request, S3Object}
import talgat.home.constants.Constant.AWS

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object S3Downloader {
    val s3Client: S3Client = S3Client.builder()
    .region(AWS.region)
    .credentialsProvider(StaticCredentialsProvider.create(AWS.credentials))
    .build()

  val dir: String = createDir()

  def getList(): IO[Unit] = IO {
    val listObjectsRequest = ListObjectsV2Request.builder().bucket(AWS.bucketName).build()
    val list = s3Client.listObjectsV2(listObjectsRequest)
    println(s"We find ${list.keyCount()} objects to download")
    list.contents().listIterator().forEachRemaining(downloadObject)
  }

  def downloadObject(s3Object: S3Object): Unit = {
    val is = s3Client.getObject(GetObjectRequest.builder().bucket(AWS.bucketName).key(s3Object.key()).build())
    FileIO.saveInputStreamToFile(is, dir + "/" + s3Object.key())
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(AWS.bucketName).key(s3Object.key()).build())
    ()
  }

  def createDir(): String = {
    val currentDate = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val dirName = currentDate.format(dateFormatter)
    val dir = new File(dirName)

    if (!dir.exists()) dir.mkdir()

    dirName
  }
}
