name := "nre-streaming-project"

version := "1.0"

scalaVersion := "2.11.8"
assemblyExcludedJars in assembly := {
    val cp = (fullClasspath in assembly).value
    cp filter { el =>
        (el.data.getName == "unused-1.0.0.jar") ||
        (el.data.getName == "spark-tags_2.11-2.1.0.jar")  
    }
}
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.1" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.1.1" % "provided"
libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.11" % "2.1.1"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.1"
libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.10.1.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.1.0"