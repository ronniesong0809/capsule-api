# Capsule API

Capsule API is a Spring Boot service for ephemeral, code-based transfer of JSON payloads. It is designed for workflows where a client needs to export structured state, receive a short human-friendly code, and let another client import that state within a limited time window.

The service uses Redis as the primary storage layer and treats share data as temporary, self-expiring state rather than durable application data.

## Highlights

- Short-code based sharing with an 8-character, ambiguity-resistant alphabet.
- Redis-backed TTL storage with atomic `SET NX` semantics.
- Versioned payload envelope using `JSON -> gzip -> base64url`.
- Clear separation between controller, service, codec, repository, model, and utility layers.
- OpenAPI 3.1 documentation through Springdoc and Swagger UI.
- Configurable payload limits, allowed payload kinds, TTL, code length, key prefix, and storage backend.
- In-memory repository for tests and Redis-free local runs.
- Guardrails against storing obvious sensitive fields such as `password`, `token`, `secret`, and `apiKey`.

## Use Case

Capsule API is useful for temporary import/export workflows such as:

- Sharing a deck, form, settings object, or UI configuration between browser sessions.
- Passing structured state across devices without requiring accounts.
- Creating short-lived transfer codes for collaborative or handoff flows.

It is intentionally not a long-term storage service. If audit history, user ownership, permanent links, or compliance retention are required, those concerns should be implemented as separate durable features.

## Architecture

```text
Client
  |
  | POST /share
  v
ShareController
  |
  v
ShareService
  |-- validates kind, size, code format, sensitive fields
  |-- allocates collision-resistant short code
  v
ShareCodec
  |-- JSON serialization
  |-- gzip compression
  |-- base64url encoding
  |-- versioned envelope
  v
ShareRepository
  |-- RedisShareRepository
  |-- InMemoryShareRepository
  v
Redis
```

## Project Structure

```text
src/main/java/com/ronniesong/capsuleapi
├── codecs          # Serialization and deserialization of stored payload envelopes
├── config          # OpenAPI metadata and share-related configuration properties
├── constants       # Shared defaults, encoding labels, and code alphabet
├── controllers     # HTTP API endpoints
├── exceptions      # API exceptions and consistent error responses
├── models
│   ├── dao         # Reserved for future database-facing models
│   ├── dto         # Internal transfer objects, such as Redis envelopes
│   ├── entity      # Runtime persistence entities
│   ├── request     # Public API request bodies
│   └── response    # Public API response bodies
├── repositories    # Storage adapters
├── services        # Business logic and validation
└── utils           # Reusable helpers
```

## Data Flow

### Create

```text
request JSON
  -> validate kind and payload size
  -> scan for sensitive field names
  -> serialize data as JSON
  -> gzip
  -> base64url
  -> wrap in envelope
  -> generate short code
  -> Redis SET key value NX EX ttl
```

### Read

```text
code
  -> normalize and validate
  -> Redis GET
  -> parse envelope
  -> base64url decode
  -> gunzip
  -> JSON parse
  -> response JSON
```

## API

`/exports` is supported as an alias for `/share`.

### Create a Share

```http
POST /share
Content-Type: application/json
```

```json
{
  "kind": "deck",
  "data": {
    "cards": []
  }
}
```

```json
{
  "code": "A8K3P9Q2",
  "expiresIn": 600
}
```

### Read a Share

```http
GET /share/{code}
```

```json
{
  "kind": "deck",
  "data": {
    "cards": []
  }
}
```

### Delete a Share

```http
DELETE /share/{code}
```

Returns `204 No Content` when the share exists and is deleted.

## Error Response

```json
{
  "error": "invalid_code",
  "message": "Invalid share code"
}
```

Common error cases:

| Status | Error | Description |
| --- | --- | --- |
| `400` | `invalid_request` | Request body is missing required fields. |
| `400` | `unsupported_kind` | `kind` is not in the configured allowlist. |
| `400` | `invalid_code` | Code has the wrong length or unsupported characters. |
| `404` | `not_found` | Code does not exist or has expired. |
| `409` | `code_collision` | A unique code could not be allocated after retries. |
| `413` | `payload_too_large` | Serialized payload exceeds the configured size limit. |
| `422` | `sensitive_payload` | Payload contains a blocked sensitive field name. |

## Storage Envelope

Redis values are stored as a versioned envelope:

```json
{
  "v": 1,
  "kind": "deck",
  "encoding": "base64url+gzip",
  "payload": "...",
  "createdAt": 1710000000000
}
```

This keeps the storage format extensible if future versions need different encodings or payload metadata.

## Short Code Design

Default alphabet:

```text
ABCDEFGHJKLMNPQRSTUVWXYZ23456789
```

The alphabet removes visually ambiguous characters such as `0/O/1/I`. With the default length of 8, the code space is:

```text
32^8 = 1,099,511,627,776
```

Code creation uses Redis `SET NX` semantics. If a generated code already exists, the service retries up to the configured limit.

## Requirements

- Java 25
- Docker and Docker Compose
- Redis 7, provided by `docker-compose.yml`
- IntelliJ or another Java IDE with Lombok annotation processing enabled

## Running Locally

Start Redis:

```bash
docker compose up -d
```

Run the API:

```bash
./gradlew bootRun
```

Health check:

```bash
curl http://localhost:8787/health
```

Create a share:

```bash
curl -X POST http://localhost:8787/share \
  -H 'content-type: application/json' \
  -d '{"kind":"deck","data":{"cards":[]}}'
```

## OpenAPI

OpenAPI JSON:

```text
http://localhost:8787/v3/api-docs
```

Swagger UI:

```text
http://localhost:8787/swagger-ui.html
```

## Configuration

Configuration lives in `src/main/resources/application.yaml`.

```yaml
server:
  port: 8787

spring:
  data:
    redis:
      url: "redis://localhost:6379"

capsule:
  share:
    store: redis
    key-prefix: "capsule:share:"
    ttl-seconds: 600
    code-length: 8
    max-payload-bytes: 262144
    max-code-attempts: 5
    allowed-kinds:
      - deck
      - config
      - form
      - settings
```

For Redis-free local experimentation:

```bash
./gradlew bootRun --args='--capsule.share.store=memory'
```

## Testing

Run the test suite:

```bash
./gradlew test
```

Tests use the in-memory repository to avoid requiring a running Redis instance.

Current coverage focuses on:

- HTTP create/read/delete flows.
- `/exports` alias behavior.
- sensitive field rejection.
- envelope encode/decode round trips.
- Spring application context startup.

## Operational Notes

- Redis is the source of truth for active share codes.
- TTL expiration is handled by Redis in the default runtime path.
- Responses set `Cache-Control: no-store` for share read/write operations.
- The service does not implement authentication, ownership, auditing, or permanent history.
- The sensitive-field scan is a guardrail, not a substitute for client-side data classification.

## IDE Notes

This project uses Lombok. If generated methods or constructors are not resolved in IntelliJ:

1. Open the project from `capsule-api/build.gradle`.
2. Reload the Gradle project.
3. Enable annotation processing.
4. Ensure the Lombok plugin is enabled.
5. Invalidate caches and restart if the index is stale.
