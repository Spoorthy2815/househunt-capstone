# HouseHunt cloud deployment
Status: deployment package prepared; no live deployment has been created yet.

## Architecture
One Railway application service serves both the React build and Spring Boot API on the same public HTTPS domain. One private Railway MySQL service persists application records. Local ports remain 5173 and 8080.
The root Dockerfile embeds React into the Spring Boot jar and runs as a non-root user. railway.json specifies Docker build and /actuator/health readiness. The server listens on PORT (8080 locally).

## Required account action
Install and connect Railway in Codex before provisioning. Hosting uses the connected Railway account's plan/credits. Any required plan purchase must be chosen by the account owner.

## Provisioning
Create project househunt with a MySQL service (persistent volume, private network) and an application service built from the repository root.
Set application variables through Railway's secrets/settings interface:
SPRING_PROFILES_ACTIVE=mysql
SEED_DEMO=false
DB_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DB_USER=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=[generated random secret, at least 32 characters]
BOOTSTRAP_ADMIN_EMAIL=[administrator email]
BOOTSTRAP_ADMIN_PASSWORD=[generated unique 12-72 character password]
CORS_ORIGIN=[application's exact public HTTPS origin]
LLM_API_KEY=[optional secret]
LLM_MODEL=gpt-4.1-mini

References assume the database service is called MySQL. React uses /api on the same origin; no separate VITE_API_URL is needed.
Do not put real secrets in source files or shell arguments. Remove bootstrap variables after initial provisioning. Existing accounts are never overwritten or promoted by bootstrap.

## Verify
Check health, homepage and direct /login navigation; register/log in; create and approve a listing; confirm search and booking workflow; restart app and check persistence.
Copy the final public HTTPS domain into the college form.
Known local demo accounts and local H2 data are not copied to the public deployment.
Local validation: 10 backend tests passed; combined jar build succeeded; homepage and direct SPA routes served React on backend port 8080; admin review API returned HTTP 200. Docker image and cloud/MySQL execution await the Railway connection.
References: https://docs.railway.com/guides/spring-boot and https://docs.railway.com/databases/mysql
