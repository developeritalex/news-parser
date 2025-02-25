FROM grafana/grafana:latest

# Устанавливаем рабочую директорию
WORKDIR /var/lib/grafana

# Копируем пользовательские дашборды и конфигурацию Grafana
COPY dashboards /var/lib/grafana/dashboards
COPY grafana.ini /etc/grafana/grafana.ini
COPY provisioning /etc/grafana/provisioning

# Открываем порт Grafana
EXPOSE 3000

# Запускаем Grafana
CMD ["/run.sh"]
