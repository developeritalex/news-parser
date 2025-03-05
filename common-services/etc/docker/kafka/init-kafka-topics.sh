#!/bin/bash

topics=(
  pgsql.demo.public.outbox
  pgsql.demo.public.newz
)

for topic in "${topics[@]}"; do
  kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic "$topic"
  execResult=$?

  if [ $execResult -ne 0 ]; then
    echo "Topic creation error: $topic. Error code: $execResult"
    exit $execResult
  fi
done

echo "All topics were successfully created!"