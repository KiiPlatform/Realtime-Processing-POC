CLASS=$1

  spark-submit \
  --class $CLASS  \
  --conf spark.ssl.truststore.password=$TRUSTSTORE_PASSWORD \
  --conf spark.ssl.keystore.password=$KEYSTORE_PASSWORD \
  --conf spark.ssl.key.password=$KEY_PASSWORD \
  --conf spark.nre.kafka.topic.id=$TOPIC_ID \
  --conf spark.nre.kafka.group.id=$GROUP_ID \
  --conf spark.nre.kafka.bootstrap.servers=$KAFKA_BOOTSTRAP_SERVERS \
  --conf spark.kii.app.host=$KII_HOST \
  --conf spark.kii.user.accesstoken=$KII_ACCESS_TOKEN \
  --conf spark.kii.app.id=$KII_APP_ID \
  --conf spark.kii.app.key=$KII_APP_KEY \
  --master local[4] \
target/scala-2.11/nre-streaming-project-assembly-1.0.jar