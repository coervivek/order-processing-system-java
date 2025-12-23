#!/bin/bash
echo "Starting application without Docker (using H2 in-memory database)..."
java -jar build/libs/oms-java-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:h2:mem:testdb \
  --spring.datasource.driver-class-name=org.h2.Driver \
  --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
  --spring.kafka.bootstrap-servers=localhost:9092
