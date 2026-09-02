# 💊 Prescription System — Medication Adherence & Prescription Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![gRPC](https://img.shields.io/badge/gRPC-1.62.2-purple.svg)](https://grpc.io/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6-black.svg)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-24.0-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **Микросервисная система выписки и отслеживания электронных рецептов с паттернами CQRS и Saga.**
>
> Проект демонстрирует современный Java-стек, событийно-ориентированную архитектуру и медицинский домен.
> *📍 Демонстрационный проект — используются только синтетические данные.*

---

## 📋 Оглавление

- [Архитектура](#-архитектура)
- [Технологический стек](#-технологический-стек)
- [Функциональность](#-функциональность)
- [Быстрый старт](#-быстрый-старт)
- [API Примеры](#-api-примеры)
- [Демо-сценарий](#-демо-сценарий)
- [Структура проекта](#-структура-проекта)
- [Команды для разработки](#-команды-для-разработки)
- [Roadmap](#-roadmap)
- [Лицензия](#-лицензия)

---

## 🏗 Архитектура
┌─────────────────────────────────────────────────────────────────────────────┐
│ Клиенты (gRPC/REST) │
└─────────────────────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ Prescription Service │
│ (gRPC Server :9090, REST :8081) │
│ • Выписка рецептов • CQRS (write: PostgreSQL) │
│ • Отмена рецептов • Saga координатор │
│ • Управление статусами • Kafka producer │
└─────────────────────────────────────────────────────────────────────────────┘
│ │
│ gRPC │ Kafka
▼ ▼
┌─────────────────────┐ ┌─────────────────────────────────────┐
│ Pharmacy Service │ │ Kafka Cluster │
│ (gRPC :9091) │◄─────────────────│ topics: │
│ • Проверка наличия │ │ • prescription-events │
│ • Резервирование │ │ • pharmacy-events │
│ • Подтверждение │ │ • compensation-events │
└─────────────────────┘ └─────────────────────────────────────┘
│
┌───────────────┼───────────────┐
▼ ▼ ▼
┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐
│ Analytics Service │ │Notification Service│ │ Audit Service │
│ (REST :8082) │ │ (REST :8083) │ │ (planned) │
│ • MongoDB (read) │ │ • Email/SMS │ │ │
│ • Отчёты │ │ • Напоминания │ │ │
└───────────────────┘ └───────────────────┘ └───────────────────┘

text

---

## 🛠 Технологический стек

| Категория | Технологии |
|-----------|------------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3.2.5, Spring Data, Spring Security |
| **gRPC** | grpc-spring-boot-starter 3.1.0, Protobuf 3.25.3 |
| **Брокер сообщений** | Apache Kafka 3.6 (Spring Kafka 3.1.5) |
| **Базы данных** | PostgreSQL 15 (write model), MongoDB 7 (read model) |
| **Микросервисные паттерны** | CQRS, Saga (компенсации), Event Sourcing |
| **Аутентификация** | Keycloak 24.0 (OAuth2 / JWT) |
| **Контейнеризация** | Docker, Docker Compose |
| **Тестирование** | JUnit 5, Testcontainers, Mockito |
| **Мониторинг** | Micrometer, Prometheus, OpenTelemetry (опционально) |

---

## 🚀 Функциональность

### ✅ Реализовано

| UC | Сценарий | Статус |
|----|----------|--------|
| UC-1 | Врач выписывает рецепт → система создаёт рецепт со статусом `ISSUED` | ✅ |
| UC-2 | Pharmacy Service проверяет наличие → публикует `PharmacyConfirmedEvent` | ✅ |
| UC-3 | Пациент получает QR-код для выкупа | ✅ |
| UC-4 | Если аптека не подтвердила за 5 минут → рецепт отменяется (компенсация) | ✅ |
| UC-5 | Фармацевт выкупает рецепт → статус `REDEEMED` | ✅ |
| UC-6 | Аналитика: % выкупленных рецептов по врачу | ✅ |
| UC-7 | Уведомления (email/SMS заглушка) об отмене/напоминании | ✅ |
| UC-8 | Аутентификация и авторизация через Keycloak (роли: DOCTOR, PATIENT, PHARMACIST) | ✅ |

### 🔜 В планах

- [ ] OpenTelemetry + Jaeger для трассировки
- [ ] Kubernetes Helm chart
- [ ] WebSocket для real-time уведомлений
- [ ] FHIR (HL7) интеграция

---

## 🚀 Быстрый старт

### Предварительные требования

```bash
# Проверьте версии
java --version          # нужна 21+
docker --version        # нужен 24.0+
docker-compose --version # нужна 2.20+
```

1️⃣ Клонирование и сборка

```bash
git clone https://github.com/yourusername/prescription-system.git
cd prescription-system

# Сборка всех модулей
mvn clean package

# Или сборка с пропуском тестов (для быстрой проверки)
mvn clean package -DskipTests

```

2️⃣ Запуск инфраструктуры (Docker Compose)

```bash
# Запуск PostgreSQL, MongoDB, Kafka, Keycloak
docker-compose up -d

# Проверка статуса контейнеров
docker-compose ps

# Просмотр логов
docker-compose logs -f
```

Сервисы и порты:

| Сервис     | Порт  | Credentials          |
|------------|-------|----------------------|
| PostgreSQL | 5432  | postgres/password    |
| MongoDB    | 27017 | -                    |
| Kafka      | 9092  | -                    |
| Keycloak   | 8080  | admin/admin          |

3️⃣ Запуск микросервисов

```bash
# В разных терминалах или в фоне
java -jar prescription-service/target/*.jar      # :8081, gRPC:9090
java -jar pharmacy-service/target/*.jar          # :8082, gRPC:9091
java -jar analytics-service/target/*.jar         # :8083
java -jar notification-service/target/*.jar      # :8084
```

Или с помощью Maven:
```bash
mvn spring-boot:run -pl prescription-service
```

4️⃣ Проверка работоспособности

```bash
# gRPC запрос к Prescription Service (нужен grpcurl)
grpcurl -plaintext -d '{
  "patient_id": "550e8400-e29b-41d4-a716-446655440000",
  "doctor_id": "660e8400-e29b-41d4-a716-446655440001",
  "medication_name": "Atorvastatin",
  "quantity": 30
}' localhost:9090 com.medical.PrescriptionService/issuePrescription

# REST запрос к Analytics Service
curl http://localhost:8083/reports/adherence/doctor-123

# Health check
curl http://localhost:8081/actuator/health
```

📡 API Примеры

gRPC (Prescription Service)

Выписка рецепта:

## Request
```json
{
  "patient_id": "550e8400-e29b-41d4-a716-446655440000",
  "doctor_id": "660e8400-e29b-41d4-a716-446655440001",
  "medication_name": "Aspirin",
  "quantity": 2
}
```

 ## Response
```json
{
  "prescription_id": "abc123-def456",
  "status": "ISSUED",
  "message": "Prescription issued! QR: https://..."
}
```

Выкуп рецепта:

```json
// Request
{
  "prescription_id": "abc123-def456",
  "pharmacy_id": "pharmacy-789"
}

// Response
{
  "success": true,
  "message": "Redeemed successfully"
}
```
## REST (Analytics Service)

```bash
# Приверженность по врачу
GET /reports/adherence/{doctorId}

# Response
{
  "doctorId": "doctor-123",
  "doctorName": "Dr. Smith",
  "totalIssued": 100,
  "totalRedeemed": 87,
  "adherenceRate": 87.0
}

# Топ врачей
GET /reports/top-doctors?limit=5
```

## 🎬 Демо-сценарий

### Сценарий 1: Счастливый путь

```bash
# 1. Врач выписывает рецепт
grpcurl -plaintext -d @ localhost:9090 com.medical.PrescriptionService/issuePrescription <<EOF
{
  "patient_id": "p123",
  "doctor_id": "d456",
  "medication_name": "Aspirin",
  "quantity": 2
}
EOF

# 2. Пациент получает QR и идёт в аптеку

# 3. Фармацевт выкупает
grpcurl -plaintext -d '{
  "prescription_id": "СКОПИРОВАТЬ_ID_ИЗ_ШАГА_1",
  "pharmacy_id": "pharm789"
}' localhost:9090 com.medical.PrescriptionService/redeemPrescription

# 4. Проверяем аналитику
curl http://localhost:8083/reports/adherence/d456
```

### Сценарий 2: Компенсация (таймаут)

```bash
# 1. Выписываем рецепт (но не выкупаем)
grpcurl -plaintext -d '...' localhost:9090 ...

# 2. Ждём 5 минут (или меньше, если изменить конфиг)

# 3. Проверяем статус — должен быть CANCELLED
grpcurl -plaintext -d '{"prescription_id": "..."}' \
  localhost:9090 com.medical.PrescriptionService/getPrescriptionStatus

# 4. Проверяем лог Notification Service
docker-compose logs notification-service | grep "CANCELLED"
```

## 📁 Структура проекта

```text
prescription-system/
├── proto/                              # gRPC определения
│   ├── prescription.proto
│   ├── pharmacy.proto
│   └── common.proto
├── common/                             # Общие классы
│   ├── src/main/java/.../events/      # Kafka события
│   └── src/main/java/.../dto/         # DTO
├── prescription-service/               # Основной сервис
│   ├── src/main/java/.../
│   │   ├── grpc/                       # gRPC реализация
│   │   ├── service/                    # Бизнес-логика
│   │   ├── repository/                 # JPA репозитории
│   │   ├── kafka/                      # Kafka producer
│   │   └── entity/                     # JPA сущности
│   └── src/main/resources/
│       └── application.yml
├── pharmacy-service/                   # Аптечный сервис
├── analytics-service/                  # Аналитический сервис (MongoDB)
├── notification-service/               # Сервис уведомлений
├── docker-compose.yml                  # Инфраструктура
└── README.md
```

## 🛠 Команды для разработки

### Maven

```bash
# Сборка всех модулей
mvn clean package

# Сборка конкретного модуля
mvn clean package -pl prescription-service

# Запуск тестов
mvn test

# Запуск интеграционных тестов (с Testcontainers)
mvn verify -Pintegration-tests
```

### Docker 
```bash
# Запуск инфраструктуры
docker-compose up -d

# Остановка
docker-compose down

# Остановка с удалением томов (очистка БД)
docker-compose down -v

# Просмотр логов конкретного сервиса
docker-compose logs -f prescription-service
```

### gRPC тестирование

```bash
# Список всех методов
grpcurl -plaintext localhost:9090 list

# Детали метода
grpcurl -plaintext localhost:9090 describe com.medical.PrescriptionService.issuePrescription
```

### Kafka

```bash
# Просмотр топиков
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Чтение сообщений из топика
docker-compose exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic prescription-events \
  --from-beginning
  ```

### 📊 Мониторинг (опционально)

```bash
# Prometheus метрики
curl http://localhost:8081/actuator/prometheus

# Логи (в реальном времени)
tail -f logs/prescription-service.log
```

### 🧪 Тестирование

```bash
# Unit тесты
mvn test

# Интеграционные тесты (требуют Docker)
mvn verify -Dtest=*IntegrationTest

# Покрытие кода (откроется в браузере)
mvn jacoco:report
open target/site/jacoco/index.html
```