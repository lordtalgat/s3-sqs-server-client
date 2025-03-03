# Server side
Run MainServer.scala class
API will response to 8080 port
GET http://localhost:8080 health check
POST http://localhost:8080/event with json

3 types of comands
case "url" => will upload directly to s3 bucket
case "sqs-create" => will send each link to SQS message
case "sqs-run" => will upload SQS message links to s3 bucket

{
    "action": "url",
    "urls": [
        "https://video.1.mp4",
        "https://video2.mp4"
    ]
}

#Client side
Run MainClient.scala class
App will create new folder with todays date if not exist.
Download all files from s3 bucket.
All downloaded files will be deleted.
