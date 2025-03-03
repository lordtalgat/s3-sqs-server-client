package talgat.home

import cats.effect.IO
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import talgat.home.constants.Constant.AWS

import java.nio.file.{Path, Paths}

object S3VideoUploader {
  def uploadFile(fileName: String): IO[String] = IO {
    val str =  if (fileName.nonEmpty) {
      // Create S3 client
      val s3Client = S3Client.builder()
        .region(AWS.region)
        .credentialsProvider(StaticCredentialsProvider.create(AWS.credentials))
        .build()

      // Create a PutObjectRequest
      val putObjectRequest = PutObjectRequest.builder()
        .bucket(AWS.bucketName)
        .key(fileName)
        .build()

      // Upload from file
      val path: Path = Paths.get(FileIO.dir + fileName)
      val obj = s3Client.putObject(putObjectRequest, path)

      // Close the S3 client
      s3Client.close()

      s"File uploaded to S3 bucket: ${AWS.bucketName} with key: $fileName and expirationDate=${obj.expiration()}"
    } else "Video file could not be downloaded"
    println(str)
    str
  }
}
