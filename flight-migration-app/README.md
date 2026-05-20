# Flight Migration App

Minimal baseline app for Java 8 + Spring Boot 2.2.13.RELEASE.

## Modules

- `auth` - HTTP Basic auth with ADMIN, AGENT, VIEWER
- `flight` - first metier module
- `booking` - second metier module
- `batch` - Spring Batch setup with `flightImportJob`
- `common` - shared API/domain support

## Requirements

- Java 8
- Maven 3.3+

Spring Boot 2.2.13.RELEASE requires Java 8 and is compatible up to Java 15. Use Java 8 for this baseline.

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Open:

```text
GET http://localhost:8080/api/me
```

Use HTTP Basic:

```text
admin / admin123
agent / agent123
viewer / viewer123
```

## Try endpoints

Create flight as admin:

```bash
curl -u admin:admin123 -H 'Content-Type: application/json' \
  -d '{"flightNumber":"EGA100","originAirportCode":"LIS","destinationAirportCode":"CDG","departureTime":"2026-06-01T10:00:00","arrivalTime":"2026-06-01T12:30:00","capacity":10}' \
  http://localhost:8080/api/flights
```

List flights as viewer:

```bash
curl -u viewer:viewer123 http://localhost:8080/api/flights
```

Create booking as agent:

```bash
curl -u agent:agent123 -H 'Content-Type: application/json' \
  -d '{"flightId":1,"passengerName":"Test User","passengerEmail":"test@example.com"}' \
  http://localhost:8080/api/bookings
```

Launch batch job as admin:

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/batch/jobs/flightImportJob/launch
```

## Notes

This is intentionally just setup. Persistence is not implemented yet; services use in-memory maps so the app can start quickly.
