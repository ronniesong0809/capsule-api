# capsule-api

Spring Boot backend for temporary short-code sharing.

Default storage is Redis. Tests use an in-memory store so they do not need Redis.

## Structure

```text
src/main/java/com/ronniesong/capsuleapi
├── controllers
├── services
├── repositories
├── codecs
├── config
├── models
│   ├── dto
│   ├── entity
│   ├── dao
│   ├── request
│   └── response
├── constants
├── utils
└── exceptions
```

## API

```http
POST /share
GET /share/{code}
DELETE /share/{code}
```

`/exports` is also supported as an alias.

Create request:

```json
{
  "kind": "deck",
  "data": {
    "cards": []
  }
}
```

Create response:

```json
{
  "code": "A8K3P9Q2",
  "expiresIn": 600
}
```

Read response:

```json
{
  "kind": "deck",
  "data": {
    "cards": []
  }
}
```

## Run

```bash
docker compose up -d
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

## Config

```properties
capsule.share.store=redis
capsule.share.key-prefix=capsule:share:
capsule.share.ttl-seconds=600
capsule.share.code-length=8
capsule.share.max-payload-bytes=262144
capsule.share.max-code-attempts=5
capsule.share.allowed-kinds=deck,config,form,settings
```
