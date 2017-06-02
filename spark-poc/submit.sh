CLASS=$1

  spark-submit \
  --class $CLASS  \
  --conf spark.ssl.truststore.password=$TRUSTSTORE_PASSWORD \
  --conf spark.ssl.keystore.password=$KEYSTORE_PASSWORD \
  --conf spark.ssl.key.password=$KEY_PASSWORD \
  --master local[4] \
target/scala-2.11/nre-streaming-project-assembly-1.0.jar