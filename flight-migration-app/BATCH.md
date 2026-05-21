# Batch

`spring.batch.job.enabled=false`; jobs launch only through ADMIN API.

Jobs:

- `flightImportJob`: reads CSV, validates flight rules, creates or updates by `flightNumber`, skips invalid rows up to 10.
- `bookingExportJob`: exports confirmed bookings to CSV, no state mutation.

Default import file:

```text
src/main/resources/batch/input/flights-sample.csv
```

Launch:

```bash
curl -u admin:admin123 -X POST "http://localhost:8080/api/batch/jobs/flightImportJob/launch"
curl -u admin:admin123 -X POST "http://localhost:8080/api/batch/jobs/flightImportJob/launch?fileName=/tmp/flights.csv"
curl -u admin:admin123 -X POST "http://localhost:8080/api/batch/jobs/bookingExportJob/launch?outputFile=target/bookings.csv"
curl -u admin:admin123 "http://localhost:8080/api/batch/jobs/flightImportJob/executions"
```

Spring Batch metadata tables are initialized in H2 with `spring.batch.initialize-schema=always`.
