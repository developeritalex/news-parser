#!/bin/bash

# Удаляем контейнер, если он существует
docker rm -f consume_container

# Удаляем volume, если он существует
docker volume rm -f consume_volume

# Запускаем новый контейнер PostgreSQL. БД будет создана автоматически по POSTGRES_USER
docker run -d --name consume_container -e POSTGRES_USER=db_consume -e POSTGRES_PASSWORD=db_consume -v consume_volume:/var/lib/postgresql/data -p 5438:5432 postgres:17-alpine

# Ждем, пока БД инициализируется
sleep 5

# Выполняем SQL-команды внутри контейнера
docker exec consume_container psql -v ON_ERROR_STOP=1 --username db_consume -d db_consume -c "CREATE SCHEMA serviceaccess;"

docker exec -it consume_container psql -U db_consume -d db_consume -c "CREATE TABLE serviceaccess.outbox (id SERIAL PRIMARY KEY, time BIGINT NOT NULL, keywords TEXT NOT NULL, text TEXT NOT NULL);"
