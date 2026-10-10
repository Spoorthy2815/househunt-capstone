# Verification — 10 October 2026
Environment: Windows 11, Java 21.0.11, Maven 3.9.16, Node 24.18.0.

Passed:
- mvn package: executable Spring Boot jar produced.
- 5 backend tests (3 integration workflows and 2 unit tests); 0 failures.
- npm run build: React production assets produced after the page/module refactor.
- npm test: 4 frontend tests passed.
- npm dependency audit after updating Vitest: 0 reported vulnerabilities.
- Headless Microsoft Edge browser smoke: seeded property listings, renter login, persistent favorite creation/removal, profile update, logout, 390px mobile page without horizontal overflow, no page runtime errors.
- Desktop and mobile previews captured in docs/.

Local servers:
- React: http://localhost:5173
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

Not verified:
- Docker Compose / MySQL execution: Docker is not installed on this machine.
- Live LLM provider responses: no API key was supplied. Database retrieval fallback and saved chat history passed integration testing.
- Cloud deployment: no provider account or target was supplied.

The full local rental lifecycle was verified against H2. MySQL configuration and container files are supplied; test that profile in the deployment environment before publishing.
Dashboard fix: admin review and owned-listing queries now call transactional service methods. Six backend tests and five frontend tests pass, including role dashboard queries and partial API failure handling.
