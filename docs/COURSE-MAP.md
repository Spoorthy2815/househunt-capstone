# Course coverage
The supplied schedule labels itself 10 weeks but enumerates 14; this capstone maps to the full 14-week list.

| Weeks | Working project implementation |
|---|---|
| 1 | Vite, React components/props/state; Spring MVC REST controllers; Axios; HTTP responses |
| 2 | Database-backed property CRUD, forms, effects, table rendering, repositories, dependency injection |
| 3 | Registration, controlled fields, Jakarta @Valid DTOs, central exception handler, user entity |
| 4 | Detail/edit routes, deletion modal, loading states, pagination/sort. User→listings @OneToMany, property→owner and booking→tenant @ManyToOne, user→profile @OneToOne, user→saved properties @ManyToMany. Explicit view DTOs prevent password leakage |
| 5 | Auth context, protected routes, USER/ADMIN, ownership checks, environment secrets |
| 6 | Spring Security filter chain, signed JWT creation/validation/expiry/issuer, BCrypt, @PreAuthorize, Axios bearer interceptor |
| 7–8 | Chat panel, Markdown, typing state, private history, async LLM integration, grounded prompts, database RAG, local hashed embeddings/cosine ranking, response token budget and request pacing |
| 9–10 | GET-only retry, error boundary, lazy images, paginated queries/indexes, @Cacheable/@CacheEvict, @Async, structured log fields, Actuator health/metrics (authenticated metrics accessible through Actuator exposure configuration) |
| 11–12 | JUnit/Mockito unit and MockMvc integration tests, frontend Vitest, production build, multistage Dockerfiles, MySQL Compose health check |
| 13–14 | OpenAPI/Swagger, environment configuration, deployment guide and integrated capstone |

The Saved homes page and property-card hearts use persisted many-to-many account favorites. Profile preferences demonstrate the one-to-one relation through an editable account form.