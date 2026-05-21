# Architecture

Modules:

- `auth`: HTTP Basic users and current-user endpoint.
- `flight`: flight API, application service, domain rules, JPA persistence.
- `booking`: booking API, application service, domain rules, JPA persistence.
- `batch`: Spring Batch job config and batch API.
- `common`: shared exceptions and API error shape.

Rules enforced by `ArchitectureRulesTest`:

- Controllers do not access repositories directly.
- Domain packages do not depend on Spring MVC, Spring Security, Spring Batch, or persistence.
- Persistence packages do not depend on API packages.
- `common` does not depend on feature modules.
- `batch` does not depend on controllers.
- `*Controller`, `*Repository`, and `*ApplicationService` live in matching packages.
