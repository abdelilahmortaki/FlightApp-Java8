# Flight Migration App

Java 8 + Spring Boot 2.2.13.RELEASE CDC baseline for flights, bookings, auth, and batch.

## Requirements

- Java 8
- Maven 3.3+

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Users:

```text
admin / admin123
agent / agent123
viewer / viewer123
```

## Endpoints

- `GET /api/me`
- `GET /api/flights`
- `GET /api/flights?page=0&size=20`
- `GET /api/flights/{id}`
- `POST /api/flights`
- `PATCH /api/flights/{id}/status`
- `PATCH /api/flights/{id}/cancel`
- `GET /api/bookings/{id}`
- `POST /api/bookings`
- `PATCH /api/bookings/{id}/cancel`
- `GET /api/batch/jobs`
- `GET /api/batch/jobs/{jobName}/executions`
- `POST /api/batch/jobs/{jobName}/launch`

## Windows Smoke

```bat
curl.exe -i http://localhost:8080/api/me
curl.exe -i -u admin:admin123 http://localhost:8080/api/me
curl.exe -i -u viewer:viewer123 http://localhost:8080/api/flights
curl.exe -i -u admin:admin123 -H "Content-Type: application/json" -d "{\"flightNumber\":\"EGA100\",\"originAirportCode\":\"LIS\",\"destinationAirportCode\":\"CDG\",\"departureTime\":\"2026-06-01T10:00:00\",\"arrivalTime\":\"2026-06-01T12:30:00\",\"capacity\":10}" http://localhost:8080/api/flights
curl.exe -i -u agent:agent123 -H "Content-Type: application/json" -d "{\"flightNumber\":\"EGA101\",\"originAirportCode\":\"LIS\",\"destinationAirportCode\":\"CDG\",\"departureTime\":\"2026-06-01T10:00:00\",\"arrivalTime\":\"2026-06-01T12:30:00\",\"capacity\":10}" http://localhost:8080/api/flights
curl.exe -i -u agent:agent123 -H "Content-Type: application/json" -d "{\"flightId\":1,\"passengerName\":\"Test User\",\"passengerEmail\":\"test@example.com\"}" http://localhost:8080/api/bookings
curl.exe -i -u viewer:viewer123 -H "Content-Type: application/json" -d "{\"flightId\":1,\"passengerName\":\"View User\",\"passengerEmail\":\"view@example.com\"}" http://localhost:8080/api/bookings
curl.exe -i -u admin:admin123 -X POST http://localhost:8080/api/batch/jobs/flightImportJob/launch
curl.exe -i -u agent:agent123 -X POST http://localhost:8080/api/batch/jobs/flightImportJob/launch
```
