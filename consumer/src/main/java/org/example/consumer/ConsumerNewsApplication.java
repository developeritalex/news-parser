package org.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.example.consumer.DebeziumAndKafkaStreams.DebeziumMessage.KAFKA_STREAM_BUILDER;
import static org.example.consumer.DebeziumAndKafkaStreams.PROPERTIES;

@Slf4j
@SpringBootApplication
public class ConsumerNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerNewsApplication.class, args);

        log.info("Initialization is starting...");
        KafkaStreams streams = new KafkaStreams(KAFKA_STREAM_BUILDER.build(), PROPERTIES);
        streams.start();
        log.info("Initialization has finished.");
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}






