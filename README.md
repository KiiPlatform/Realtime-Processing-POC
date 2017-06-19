# Realtime-Processing-POC

## System Design

![](/images/architecture.png)

## Related Repository

- [velocity-mock](https://github.com/KiiPlatform/velocity-mock)
  Node JS application to generate mock data. (No. 3 on the system design)

- [spark-notebook-nre](https://github.com/KiiPlatform/spark-notebook-nre)
  Notebooks project for setup and visualization (No. 2 on the system design)

## Pre Requirements

- JDK 8
- Spark 2.1.x
- scala 2.1.18
- scala build tool (SBT). 
- MQTT broker installed. 
- Download and start kafka 10.x. Please refer to : https://kafka.apache.org/quickstart
    - ``` bin/zookeeper-server-start.sh config/zookeeper.properties ```
    - ``` bin/kafka-server-start.sh config/server.properties ```
- Download and start spark-notebook latest version.

## Execution guides

1. Clone [velocity-mock](https://github.com/KiiPlatform/velocity-mock) repository
2. Go to `spark-notebook` installation directory.
3. Go to `notebooks` folder then clone [spark-notebook-nre](https://github.com/KiiPlatform/spark-notebook-nre)
4. Follow guidelines on this `spark-poc/README.md`
5. Makesure local kafka, and mqtt broker is up and running.
6. Open and execuute `structured-streaming.snb` 
7. Run the `velocity-mock`
8. Confirm that the `realtime` average chart is working.