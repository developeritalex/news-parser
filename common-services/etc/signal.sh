#!/bin/bash

# В реальных проектах чаще всего отправка events именно через Kafka (с помощью этого скрипта),
# а не DB, так как у разработчиков нет доступа к DB в продакшне.


# Параметры Kafka
BOOTSTRAP_SERVER="localhost:9092"
TOPIC="pgsql.demo.kafka.signal"

# Сообщение
KEY="pgsql.demo"
VALUE='{"type":"execute-snapshot", "data": {"data-collections": ["public.outbox"], "type": "incremental"}}'

# Отправка сообщения в Kafka
echo "${KEY}:${VALUE}" | kafka-console-producer \
  --bootstrap-server "${BOOTSTRAP_SERVER}" \
  --topic "${TOPIC}" \
  --property "parse.key=true" \
  --property "key.separator=:"

echo "Сообщение отправлено в Kafka топик '${TOPIC}'"