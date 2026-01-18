# Orders Kafka Microservice

## Overview

This is a minimal but **production-grade Kafka-based microservice** for order events. It demonstrates:

* Publishing and consuming Kafka messages using Spring Boot
* **Retryable topics** with exponential backoff using `@RetryableTopic`
* Dead Letter Topic (DLT) handling with safe reprocessing
* Manual acknowledgment and robust error handling
* Integration with **Avro serialization** (via Schema Registry)

The project is structured to illustrate **end-to-end failure handling**, including retries, DLT quarantine, and safe replay.

---
## Features

| Feature                   | Description                                                                                |
| ------------------------- | ------------------------------------------------------------------------------------------ |
| Kafka Producer            | Publishes `OrderEvent` messages using `KafkaTemplate<String, OrderEvent>`                  |
| Kafka Consumer            | Listens to `orders` topic and processes messages with retry support                        |
| Retryable Topics          | Configured via `@RetryableTopic` with backoff, exponential multiplier, and DLT integration |
| Dead Letter Topic (DLT)   | Non-recoverable messages are sent to `orders.DLT` and consumed via a dedicated reprocessor |
| Manual Ack                | All DLT and retry processing use manual acknowledgment to prevent message loss             |
| Scheduler                 | TaskScheduler orchestrates retry backoff timing                                            |
| Infrastructure Separation | Business and infrastructure KafkaTemplates separated for clarity and safety                |

---

## Architecture

```
Client / Producer
       |
       v
   Kafka Topic: orders
       |
       v
   @KafkaListener (OrderConsumer)
       |-- Success: processing complete
       |-- Failure: retry topics (orders-RETRY-1000, 2000, 4000)
       |-- Non-retryable: DLT (orders.DLT)
       |
       v
   DLT Listener (OrderDltReprocessor)
       |-- Archive / Inspect / Republish
```

**Notes:**

* Retry topics are automatically created with delay appended to the topic name:
  `orders-RETRY-<delay-ms>`
* DLT messages are not auto-reprocessed; safe manual replay is required.
* `TaskScheduler` is required for `@RetryableTopic` to handle delayed retries.

---

## Configuration

### application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
      group-id: order-consumer-group
      auto-offset-reset: earliest
    properties:
      schema.registry.url: http://localhost:8081
```

---

### Retry Configuration (Spring Bean)

```java
@Bean
public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setThreadNamePrefix("kafka-retry-scheduler-");
    scheduler.initialize();
    return scheduler;
}
```

### Error Handling (DLT + Retryable Exceptions)

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(
                    kafkaTemplate,
                    (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
            );

    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer);
    handler.addRetryableExceptions(RuntimeException.class);
    handler.addNotRetryableExceptions(IllegalArgumentException.class);

    return handler;
}
```

---

## Usage

### Producing Messages

You can produce test messages using:

```json
{
  "orderId": "order-001",
  "product": "Banana",
  "quantity": 10,
  "price": 3.50
}
```

Simulate failures:

```json
{
  "orderId": "fail-order-002",
  "product": "Notebook",
  "quantity": -3,
  "price": 4.00
}
```

Non-retryable failures (e.g., validation errors) go directly to `orders.DLT`.

---

### Safe DLT Reprocessing

```java
@KafkaListener(
    topics = "orders.DLT",
    groupId = "orders-dlt-reprocessor",
    containerFactory = "dltKafkaListenerContainerFactory"
)
public void onDltMessage(ConsumerRecord<Object, Object> record, Acknowledgment ack) {
    if (isSafeToReprocess(record)) {
        kafkaTemplate.send(getOriginalTopic(record), record.key(), record.value());
    } else {
        archive(record);
    }
    ack.acknowledge();
}
```

**Rules:**

* Manual acknowledgment ensures **no accidental loss**
* Validation exceptions are **never replayed**
* Archive messages for audit purposes

---

## Observability

* Thread names for retries: `kafka-retry-scheduler-<n>`
* Retry topic names include the **delay in milliseconds**
* Logging includes **original topic, partition, offset, and exception** for DLT messages

---

## Running the Project

1. Start Kafka & Schema Registry (Docker recommended)
2. Build the project:

```bash
mvn clean install
```

3. Run Spring Boot:

```bash
mvn spring-boot:run
```

4. Produce test messages via API or Kafka CLI
5. Observe retry topics and DLT in Kafka logs

---

## Key Lessons Demonstrated

* Handling poison messages with **retry topics and DLT**
* Backoff scheduling with **TaskScheduler**
* Infrastructure isolation (separate KafkaTemplates for business vs retry/DLT)
* Safe and observable reprocessing pipeline
* Clear exception classification for retry/non-retryable logic

---

## License

This project is provided for educational and demonstration purposes.

---
