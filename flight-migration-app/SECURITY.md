# Security

HTTP Basic is for migration baseline and smoke tests only. Do not use these static users in production.

| Area | ADMIN | AGENT | VIEWER |
|---|---:|---:|---:|
| `GET /api/me` | yes | yes | yes |
| flight read | yes | yes | yes |
| flight write | yes | no | no |
| booking read | yes | yes | no |
| booking create/cancel | yes | yes | no |
| batch | yes | no | no |
| H2 console | yes | no | no |

Security stays on `WebSecurityConfigurerAdapter` because Spring Boot 2.2 uses Spring Security 5.x. Code marks this as Boot 3+ migration debt.
