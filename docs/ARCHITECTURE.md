# Architecture and API
React → Axios → Spring Security JWT filter → controller/DTO validation → service → Spring Data JPA → MySQL (H2 locally).
Property search uses a cache. Listing creation/edit/moderation/deletion evicts cached search results.
Bookings lock the property row when requesting/accepting, so concurrent acceptances cannot reserve overlapping dates.
Dates are half-open [start, end): an incoming booking may start on another booking's move-out date.
Users own properties. Tenants request bookings; owners respond. Admin moderation is separate from public browsing.
Passwords are BCrypt hashes. Registration rejects role injection. Public DTOs never include a password or owner's email.
Profile and favorites relations have authenticated /api/account endpoints.

## API groups
- POST /api/auth/register, /api/auth/login; GET /api/auth/me
- GET /api/properties?city=&type=&min=&max=&q=&page=&sort=createdAt
- GET/PUT/DELETE /api/properties/{id}; POST /api/properties
- GET /api/dashboard/properties
- GET/PATCH /api/admin/properties (PATCH uses /{id}); GET /api/admin/users
- GET/POST /api/bookings; PATCH /api/bookings/{id}
- GET/POST /api/chat
- GET/PUT /api/account/profile
- GET /api/account/favorites; PUT/DELETE /api/account/favorites/{id}

Swagger exposes generated request/response schemas and is the API testing reference. Use an Authorization: Bearer token header for secured operations.

## Deployment
Build backend with mvn package and frontend with npm ci && npm run build.
Deploy the backend container or Java jar on a Java/container platform; provide a MySQL service and environment variables from .env.example. Disable demo seed. Configure CORS_ORIGIN to the exact HTTPS frontend origin.
Deploy frontend/dist on a static host. Before building, set VITE_API_URL to the HTTPS backend /api endpoint. Configure a rewrite to /index.html for client-side React routes.
Alternatively deploy the supplied Compose setup with a reverse proxy and TLS. The supplied Compose ports are for local use.
Cloud deployment is not performed automatically: it needs the user's provider account and database credentials.
GET /actuator/health is public; other monitoring endpoints require authentication. Do not publicly expose sensitive diagnostics.
The default schema-update setup is for capstone development; production should use migrations, backups, centralized observability and shared cache/rate limits.

## Data and external dependencies
The UI references Unsplash images and optional Google fonts. Local fallback assets keep listings usable if images fail.
LLM keys never reach the browser. Only the current prompt and public approved listing snippets go to the configured provider.