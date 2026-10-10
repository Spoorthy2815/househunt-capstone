# HouseHunt
A working rental capstone built with React, Spring Boot 3, Java 21, and MySQL. The local profile uses a persistent H2 database so the project can be tried immediately.

For the final ports, reset command, and demo workflow, see [docs/FINAL-WORKFLOW.md](docs/FINAL-WORKFLOW.md).

## Run locally
Prerequisites: Node.js 22.12+ (or 24), Java 21, Maven 3.9+.
Open two terminals in this directory:
1. Backend: `cd backend`, then `mvn spring-boot:run`.
2. Frontend: `cd frontend`, then `npm ci`, then `npm run dev`.
Open http://localhost:5173. Backend health: http://localhost:8080/actuator/health.
Swagger UI: http://localhost:8080/swagger-ui/index.html.
The backend persists local data under backend/data. Do not run two backend instances against the same H2 file.

### Local demonstration accounts
| Role | Email | Password |
|---|---|---|
| Admin | admin@househunt.local | AdminDemo123! |
| Owner | owner@househunt.local | OwnerDemo123! |
| Renter | renter@househunt.local | RenterDemo123! |
Only the local profile seeds these accounts by default. Never enable demo seed on a public deployment. Registration always creates a USER; users cannot self-assign administrator privileges.

## Try the complete workflow
1. Browse, filter by city/type/budget, sort by price, save a home, and inspect its detail page.
2. Register or log in as the renter. Request a future date range.
3. Log in as owner to accept or decline the request in the dashboard. Accepted date ranges cannot overlap.
4. Create or edit a listing as owner. It enters PENDING status.
5. Log in as administrator. Review its details, then approve/reject it. Only approved homes appear publicly.
6. Use the assistant for listing suggestions; history is stored separately for each user.
7. Delete a listing without booking history. Listings with booking history are retained to preserve records.

## MySQL and Docker
Install Docker separately (it was not available in the development environment).
Copy .env.example to .env and replace all passwords and JWT_SECRET.
Run `docker compose up --build`, then open http://localhost:3000.
MySQL is private to the container network, with a named persistent volume and readiness check.
For a private demonstration, explicitly set SEED_DEMO=true before first startup.
For a public installation, leave it false. Create an account, then an operator can promote that exact account using parameterized SQL:
`UPDATE users SET role='ADMIN' WHERE email='your-admin@example.com';`
Sign in again after promotion. Never expose a public promotion endpoint.

For a native MySQL deployment set SPRING_PROFILES_ACTIVE=mysql, DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET and CORS_ORIGIN in the backend process environment. Spring Boot does not automatically load .env; Docker Compose does. H2 is only the quick-start profile.

## AI / retrieval
Set LLM_API_KEY, optionally LLM_MODEL and LLM_API_URL in the backend environment, then restart.
The integration calls an OpenAI-compatible chat-completions endpoint on an async worker with connect/read deadlines, a 500-token response budget, and per-user pacing.
The retrieval pipeline embeds query and listing text into normalized 256-dimensional local feature-hashing vectors, uses cosine similarity, and injects the five best approved listings into the prompt.
This is a runnable introduction to embeddings/vector retrieval, not neural semantic embeddings or an external vector database. It searches up to 100 approved candidates.
Without a key, the assistant returns deterministic database recommendations, clearly labeled. Provider errors also fall back to database results.
Conversation history is stored, but only the current question and retrieved listing context are sent to the provider to limit cost. Booking is always performed through the authorized booking API.

## Verification
Backend: `cd backend` then `mvn test`.
Frontend: `cd frontend` then `npm test` and `npm run build`.
Integration tests cover registration, password validation, JWT login, ownership restrictions, admin permissions, visibility, search accuracy, booking transitions, overlap prevention, history retention and chat persistence.
Unit tests cover cross-owner deletion and vector retrieval ranking.
Frontend tests cover token cleanup, INR formatting and actionable API errors.
See docs/COURSE-MAP.md for syllabus coverage and docs/ARCHITECTURE.md for design and deployment notes.

## Optional browser smoke test
With both servers running and Microsoft Edge installed, install Playwright in a separate test environment and run scripts/browser-smoke.cjs (or set PLAYWRIGHT_MODULE to your Playwright package path). It checks desktop/mobile rendering, login, saved homes, profile persistence, logout and runtime errors. It expects the original local demo seed. Preview PNGs are saved under docs.

## Practical boundaries
This is a rental management application. It does not process payments, execute legal contracts, supply live market prices, or verify property ownership. Virtual tours are owner-provided HTTPS links; images are owner-provided URLs.
Demo photos and optional fonts require internet access; property images have a bundled fallback.
JWTs expire after two hours and remain valid until expiry after client logout. Tokens are held in sessionStorage. Production hardening can add refresh-token rotation/revocation, account verification, password reset, login throttling, content scanning and audit retention.
Schema creation uses Hibernate update for easy local setup; use reviewed migration scripts before production releases. Caching and pacing are per-process; a multi-instance deployment needs shared cache/rate limiting.