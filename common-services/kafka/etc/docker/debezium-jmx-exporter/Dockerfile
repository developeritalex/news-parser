FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /kafka

# Копируем JMX Exporter и конфиг
# Предполагается, что файл jmx_prometheus_javaagent.jar находится в директории,
# которая является частью контекста сборки.
RUN ls -l ./etc/docker/debezium-jmx-exporter
COPY ./etc/docker/debezium-jmx-exporter/jmx_prometheus_javaagent-0.17.2.jar /kafka/jmx_prometheus_javaagent.jar
COPY /Users/aleksandrtufanov/IdeaProjects/news-parser/common-services/kafka/etc/docker/debezium-jmx-exporter/config.yml /kafka/config.yml

# Указываем порт для JMX Exporter
EXPOSE 8080

# Запускаем JMX Exporter
CMD ["java", "-javaagent:/kafka/jmx_prometheus_javaagent.jar=8080:/kafka/config.yml"]