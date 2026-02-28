# entsoe-db-service

A Spring Boot microservice that consumes ENTSO-E electricity spot price data from a JMS queue, parses the XML payload, and persists the prices to a MySQL database. A REST API is exposed to query the stored prices.

## Features

- Consumes ENTSO-E XML messages from an Apache Artemis JMS queue (`entsoe.prices`) via Apache Camel
- Parses hourly/sub-hourly spot price data including timestamp, price, currency, and bidding-zone area
- Persists prices to a MySQL database using Spring Data JPA
- REST API to retrieve stored prices
- Spring Boot Actuator endpoints for health and metrics
- Registered as a client with Spring Boot Admin

## Tech Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 4.0.3 (Java 21) |
| Database | MySQL with Spring Data JPA / Hibernate |
| Messaging | Apache Artemis (ActiveMQ) |
| Message routing | Apache Camel 4.18.0 |
| Monitoring | Spring Boot Admin, Actuator, Jolokia |

## REST API

Base path: `/api/prices`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/prices` | Return all stored spot prices |
| `GET` | `/api/prices/area/{area}` | Return prices for a specific bidding-zone area, ordered by timestamp descending |
| `GET` | `/api/prices/today` | Return prices for the current calendar day, ordered by timestamp |

## Configuration

All configurable values are listed in `src/main/resources/application.properties`. Sensitive values are read from environment variables with fallback defaults shown in the table below.

| Property | Environment Variable | Default |
|----------|----------------------|---------|
| MySQL username | `MYSQL_USER` | `entsoe` |
| MySQL password | `MYSQL_PASSWORD` | `entsoe` |
| Artemis username | `ARTEMIS_USER` | `admin` |
| Artemis password | `ARTEMIS_PASSWORD` | `admin` |
| Spring Security username | `SPRING_SECURITY_USER_NAME` | `user` |
| Spring Security password | `SPRING_SECURITY_USER_PASSWORD` | `yourpassword` |

Other notable defaults:

- **Server port**: `8087`
- **MySQL URL**: `jdbc:mysql://192.168.0.6:3307/entsoe`
- **Artemis broker URL**: `tcp://192.168.0.6:61616`
- **Spring Boot Admin URL**: `http://192.168.0.6:8085`
- **Log file**: `/app/logs/application.log`

## Building and Running

### Prerequisites

- Java 21
- Maven (or use the included `mvnw` wrapper)
- A running MySQL instance
- A running Apache Artemis broker

### Build

```bash
./mvnw clean package
```

### Run

```bash
./mvnw spring-boot:run
```

Or after building:

```bash
java -jar target/entsoe-db-service-0.0.1-SNAPSHOT.jar
```

Override configuration values via environment variables as needed:

```bash
MYSQL_USER=myuser MYSQL_PASSWORD=secret ./mvnw spring-boot:run
```

## Database Schema

The service manages a `spot_prices` table (created/updated automatically by Hibernate):

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Auto-generated identifier |
| `timestamp` | DATETIME | Price interval start time |
| `price` | DECIMAL | Spot price value |
| `currency` | VARCHAR | Currency code (e.g. `EUR`) |
| `area` | VARCHAR | Bidding-zone area code (e.g. `SE3`) |
