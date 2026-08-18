# PayFlow

A backend payment service built with Spring Boot 4.1, Spring Data JPA, PostgreSQL, and Docker — built step by step as a learning project covering Spring fundamentals through to containerized deployment, database migrations, integration testing, and JWT authentication.

## Tech Stack

- **Java 25**, **Spring Boot 4.1.0**
- **Spring Web (MVC)** — REST API layer
- **Spring Data JPA** (Hibernate) — persistence
- **PostgreSQL 17** — database, run via Docker
- **Flyway** — versioned database migrations
- **Spring Security** — API key and JWT authentication
- **JJWT** — JWT generation and validation
- **Bean Validation** — request validation
- **JUnit 5 + Mockito** — unit testing
- **Testcontainers** — isolated integration testing with a real, throwaway Postgres instance
- **Docker & Docker Compose** — containerization

## Features

- Create and retrieve payments (`POST /payments`, `GET /payments/{id}`)
- Refund payments (`PUT /payments/{id}/refund`)
- **Idempotency key support** — safe retries without duplicate charges, enforced at both the application and database level (unique constraint)
- Enum-based payment status (`PENDING`, `SUCCESS`, `FAILED`, `REFUNDED`)
- **Transaction audit log** — every payment status change is recorded immutably (creation, refund, etc.), linked via a one-to-many relationship
- Global exception handling with clean, structured error responses (validation errors, not-found, malformed requests)
- **Dual authentication** — API key (`X-API-KEY` header) and JWT (`Authorization: Bearer <token>`), either accepted
- Versioned schema migrations via Flyway (no auto-generated DDL in production)

## Running Locally (without Docker)

1. Start PostgreSQL:

docker run --name payflow-postgres -e POSTGRES_DB=payflow -e POSTGRES_USER=payflow_user -e POSTGRES_PASSWORD=payflow_pass -p 5432:5432 -d postgres:17

2. Run the app from IntelliJ, or:

mvn spring-boot:run

3. App available at `http://localhost:8080`

Flyway applies any pending migrations automatically on startup.

## Running with Docker Compose (recommended)

Starts both the app and PostgreSQL together:

docker compose up --build


App available at `http://localhost:8080`. Data persists in a named Docker volume (`payflow-pgdata`) across restarts.

## Authentication

Every endpoint except `/auth/login` requires authentication via **either**:

**API Key**

X-API-KEY: <your-api-key>


**JWT** — obtain a token first:

POST /auth/login
Content-Type: application/json

{
"clientId": "payflow-client",
"clientSecret": "payflow-secret"
}

Returns a signed JWT (valid for 1 hour). Use it on subsequent requests:

Authorization: Bearer <token>


## API Usage

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

Response includes the full transaction log:
```json
{
  "id": 3,
  "amount": 100.50,
  "currency": "INR",
  "status": "REFUNDED",
  "idempotencyKey": "unique-client-generated-key",
  "logs": [
    { "previousStatus": null, "newStatus": "PENDING", "changedAt": "..." },
    { "previousStatus": "PENDING", "newStatus": "REFUNDED", "changedAt": "..." }
  ]
}
```

**Refund a payment**

PUT /payments/{id}/refund
X-API-KEY: <your-api-key>


## Database Migrations

Schema changes are managed with Flyway. Migration files live in `src/main/resources/db/migration`, named `V<number>__description.sql` (e.g. `V2__create_transaction_logs_table.sql`). Flyway applies any pending migrations automatically on application startup.

## Running Tests

mvn test


Includes a unit test for payment creation/idempotency logic (mocked repositories) and a Spring context load test that uses Testcontainers to spin up a real, throwaway PostgreSQL instance automatically — no manual database setup required.

## Configuration

Key properties in `application.properties`:

| Property | Purpose |
|---|---|
| `spring.datasource.url/username/password` | Postgres connection |
| `spring.jpa.hibernate.ddl-auto` | Set to `validate` — schema is managed by Flyway, this just verifies entities match |
| `payflow.security.api-key` | API key accepted on all requests |
| `payflow.security.jwt-secret` | Secret used to sign/verify JWTs (min 256 bits) |

## Notes

- The `/auth/login` credentials are a hardcoded single client, suitable for learning; a production system would validate against a real client/user store.
- The API key remains supported alongside JWT for backward compatibility during this learning progression.