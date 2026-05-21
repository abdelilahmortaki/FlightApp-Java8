# Migration Plan

Baseline:

```text
Java 8
Spring Boot 2.2.13.RELEASE
javax.* namespace
Spring Security 5.x WebSecurityConfigurerAdapter style
Spring Batch via spring-boot-starter-batch
```

Path:

```text
Boot 2.2.13 -> Boot 2.7.18 -> Java 21 -> Boot 3.x -> Boot 4.x
```

Notes:

- Stay on `javax.*` until Boot 3 migration.
- Keep `WebSecurityConfigurerAdapter` until Boot 3 introduces `SecurityFilterChain`.
- Move through Boot 2.7 first to reduce dependency and deprecation drift.
- Upgrade Java after Boot 2.7 is stable, then migrate Spring Framework 6 / Jakarta APIs with Boot 3.
