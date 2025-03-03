package talgat.home

import cats.effect.IO
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{DeleteMessageRequest, ReceiveMessageRequest, SendMessageRequest}
import talgat.home.constants.Constant.AWS

object SQSService {
  private val queueUrl = AWS.queueUrl
  private val region = AWS.region

  def produceMessage(message: String): IO[Unit] = IO {
    try {
      // Create a SendMessageRequest
      val sendMessageRequest = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(message)
        .build()

      val sqsClient = SqsClient.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(AWS.credentials))
        .build()

      // Send the message to the SQS queue
      val sendMessageResponse = sqsClient.sendMessage(sendMessageRequest)

      // Print the message ID (for confirmation)
      println(s"Message sent with ID: ${sendMessageResponse.messageId()}")

      // Close the SQS client
      sqsClient.close()
    } catch {
      case e: Exception => println(e.fillInStackTrace())
    }
  }

  def consumeMessage(): IO[Unit] = IO {
    println("sqs started to list")
    val receiveMessageRequest = ReceiveMessageRequest.builder()
      .queueUrl(queueUrl)
      .maxNumberOfMessages(5) // Fetch up to 5 messages at a time
      .waitTimeSeconds(20) // Long polling (wait up to 10 seconds for messages)
      .build()

    val sqsClient = SqsClient.builder()
      .region(region)
      .credentialsProvider(StaticCredentialsProvider.create(AWS.credentials))
      .build()

    // Receive messages from the queue
    val receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest)
    val messages = receiveMessageResponse.messages().listIterator()

    // Process each message
    messages.forEachRemaining { message =>
      try {
        FileIO.download(message.body()).map { fileName =>

          val deleteMessageRequest = DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(message.receiptHandle())
            .build()

          val result = for {
            _ <- S3VideoUploader.uploadFile(fileName)
            _ <- FileIO.delete(fileName)
            delObj = sqsClient.deleteMessage(deleteMessageRequest)
          } yield delObj
          val delObj = result.unsafeRunSync()
          println(s"Deleted message with ID: ${delObj.responseMetadata()}")
        }
      } catch {
        case e: Exception => println(e.getMessage)
      }
    }

    // Close the SQS client
    sqsClient.close()
  }
}
