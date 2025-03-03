package talgat.home.constants

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region

object Constant {
 object AWS {
   private val awsAccessKey = "awsAccessKey"
   private val awsSecretKey = "awsSecretKey"
   val credentials: AwsBasicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
   val region: Region = Region.US_EAST_1
   val bucketName: String = "bucketName"
   val queueUrl: String = "queueUrl"
 }
}
