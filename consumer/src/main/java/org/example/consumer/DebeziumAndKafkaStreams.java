package org.example.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class DebeziumAndKafkaStreams {

    static final Properties PROPERTIES = new Properties();

    static {
        PROPERTIES.put(StreamsConfig.APPLICATION_ID_CONFIG, "demo-debezium-group-2");
        PROPERTIES.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPERTIES.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        PROPERTIES.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DebeziumMessage<T> {
        private String op;
        private T before;
        private T after;
        private Object source;
        private Long ts_ms;
        private Long ts_us;
        private Long ts_ns;
        private Object transaction;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class NewsState {
            private String id;
            private Long time;
            private String keywords;
            private String text;
        }

        public static class JsonSerializer<T> implements Serializer<DebeziumMessage<T>> {
            private final ObjectMapper objectMapper;

            public JsonSerializer() {
                this.objectMapper = new ObjectMapper();
                this.objectMapper.registerModule(new JavaTimeModule());
                this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }

            @Override
            public byte[] serialize(String topic, DebeziumMessage<T> data) {
                if (data == null) {
                    return null;
                }
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing JSON message", e);
                }
            }
        }

        public static class JsonDeserializer<T> implements Deserializer<DebeziumMessage<T>> {
            private final ObjectMapper objectMapper;
            private final Class<T> valueTypeClass;

            public JsonDeserializer(Class<T> valueTypeClass) {
                this.valueTypeClass = valueTypeClass;
                this.objectMapper = new ObjectMapper();
                this.objectMapper.registerModule(new JavaTimeModule());
                this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }

            @Override
            public DebeziumMessage<T> deserialize(String topic, byte[] data) {
                if (data == null) {
                    return null;
                }
                try {
                    String jsonData = new String(data, StandardCharsets.UTF_8);
                    log.info("Deserializing JSON: {}", jsonData);

                    return objectMapper.readValue(data,
                            objectMapper.getTypeFactory().constructParametricType(
                                    DebeziumMessage.class,
                                    valueTypeClass
                            ));
                } catch (IOException e) {
                    log.error("Failed to deserialize: {}", e.getMessage(), e);
                    throw new RuntimeException("Error deserializing JSON message", e);
                }
            }
        }

        public static final Serde<DebeziumMessage<NewsState>> DEBEZIUM_NEWS_SERDE = Serdes.serdeFrom(
                        new JsonSerializer<NewsState>(),
                        new JsonDeserializer<>(NewsState.class)
        );

        static final StreamsBuilder KAFKA_STREAM_BUILDER = new StreamsBuilder();

        static {
            Materialized<String, DebeziumMessage<DebeziumMessage.NewsState>, KeyValueStore<Bytes, byte[]>> store =
                    Materialized.as("news-state-store");

            KAFKA_STREAM_BUILDER
                    .stream("pgsql.demo.public.outbox", Consumed.with(Serdes.String(), DebeziumMessage.DEBEZIUM_NEWS_SERDE))
                    .peek((key, value) -> log.info("RECEIVED NEWS: {}", value))
                    .to("news-topic", Produced.with(Serdes.String(), DebeziumMessage.DEBEZIUM_NEWS_SERDE));
        }
    }
}