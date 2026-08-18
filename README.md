# PayFlow

A backend payment service built with Spring Boot 4.1, Spring Data JPA, PostgreSQL, and Docker — built step by step as a learning project covering Spring fundamentals through to containerized deployment.

## Tech Stack

- **Java 25**, **Spring Boot 4.1.0**
- **Spring Web (MVC)** — REST API layer
- **Spring Data JPA** (Hibernate) — persistence
- **PostgreSQL 17** — database, run via Docker
- **Spring Security** — custom API key authentication
- **Bean Validation** — request validation
- **JUnit 5 + Mockito** — unit testing
- **Docker & Docker Compose** — containerization

## Features

- Create and retrieve payments (`POST /payments`, `GET /payments/{id}`)
- Refund payments (`PUT /payments/{id}/refund`)
- **Idempotency key support** — safe retries without duplicate charges, enforced at both the application and database level (unique constraint)
- Enum-based payment status (`PENDING`, `SUCCESS`, `FAILED`, `REFUNDED`)
- Global exception handling with clean, structured error responses (validation errors, not-found, malformed requests)
- API key authentication via a custom Spring Security filter
- Automated schema management via Hibernate (`ddl-auto=update`)

## Running Locally (without Docker)

1. Start PostgreSQL:
   docker run --name payflow-postgres -e POSTGRES_DB=payflow -e POSTGRES_USER=payflow_user -e POSTGRES_PASSWORD=payflow_pass -p 5432:5432 -d postgres:17

2. Run the app from IntelliJ, or:

mvn spring-boot:run

3. App available at `http://localhost:8080`

## Running with Docker Compose (recommended)

Starts both the app and PostgreSQL together:

docker compose up --build


App available at `http://localhost:8080`. Data persists in a named Docker volume (`payflow-pgdata`) across restarts.

## API Usage

All endpoints require an `X-API-KEY` header.

**Create a payment**

POST /payments
X-API-KEY: <your-api-key>
Content-Type: application/json

{
"amount": 100.50,
"currency": "INR",
"idempotencyKey": "unique-client-generated-key"
}


**Get a payment**

GET /payments/{id}
X-API-KEY: <your-api-key>


**Refund a payment**

PUT /payments/{id}/refund
X-API-KEY: <your-api-key>


## Running Tests

mvn test


Includes a unit test for the payment creation/idempotency logic (mocked repository) and a Spring context load test.

## Configuration

Key properties in `application.properties`:

| Property | Purpose |
|---|---|
| `spring.datasource.url/username/password` | Postgres connection |
| `spring.jpa.hibernate.ddl-auto` | Schema auto-generation (dev only — not production-safe) |
| `payflow.security.api-key` | API key required on all requests |

## Notes

- `ddl-auto=update` is used for learning convenience; a production system would use Flyway or Liquibase migrations instead.
- The API key is a simple shared-secret scheme suitable for learning; a production system would likely use JWT or OAuth2.