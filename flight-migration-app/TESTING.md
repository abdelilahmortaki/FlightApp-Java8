# Testing

Commands:

```bash
mvn -Dtest=*Flight* test
mvn -Dtest=*Booking* test
mvn -Dtest=*Security* test
mvn -Dtest=*Batch*,*FlightImport* test
mvn -Dtest=ArchitectureRulesTest test
mvn clean test
```

Behavior test list stays at 20:

- Spring context loads.
- Flight create/read/list/page/status/cancel.
- Flight invalid same origin/destination, departure after arrival, duplicate number, invalid capacity.
- Departed flight cannot be modified.
- Booking create/read, seat decrement, duplicate passenger, invalid email, cancelled/full flight blocked, cancel restores seat, cancel twice blocked.
- Security: unauthenticated `/api/me`, viewer cannot create booking, agent cannot launch batch.
- Batch import skips invalid rows.
