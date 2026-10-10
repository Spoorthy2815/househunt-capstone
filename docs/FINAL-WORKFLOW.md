# Final local workflow
Use only frontend port 5173 and backend port 8080. Do not start additional instances.

## Stop/reset all HouseHunt instances
From PowerShell:
cd "G:\data c\Documents\ChatGPT\HOUSEHUNT"
powershell -NoProfile -ExecutionPolicy Bypass -File ".\scripts\Stop-HouseHunt.ps1"

This stops only Node/Java HouseHunt processes whose command or argument file points to this project. It does not delete database files or stop unrelated projects.

## Start backend — Terminal 1
cd "G:\data c\Documents\ChatGPT\HOUSEHUNT\backend"
mvn spring-boot:run

Wait for the application to start on port 8080. Leave this terminal running.

## Start frontend — Terminal 2
cd "G:\data c\Documents\ChatGPT\HOUSEHUNT\frontend"
npm ci
npm run dev

npm ci is needed for first setup or dependency changes; ordinary restarts only need npm run dev.
Vite uses 5173 with strictPort enabled; it will fail if that port is occupied instead of selecting 5174.

## URLs
- Computer app: http://localhost:5173
- Phone app on the same Wi-Fi: http://192.168.31.97:5173 (IP may change when your network changes)
- Backend health: http://localhost:8080/actuator/health
- Swagger: http://localhost:8080/swagger-ui/index.html

The frontend proxies /api to localhost:8080; the phone needs access only to frontend port 5173.
There is no separate database port in the default H2 local profile. MySQL runs only when explicitly configured.

## Test the workflow
1. Renter login: renter@househunt.local / RenterDemo123!
2. Browse an approved property and request a future date range.
3. Log out. Owner login: owner@househunt.local / OwnerDemo123!
4. Open My dashboard to accept or decline the booking.
5. As owner, create a listing; it enters PENDING.
6. Log out. Admin login: admin@househunt.local / AdminDemo123!
7. Review and approve the listing in My dashboard.
8. Verify it appears publicly. Test saved homes and the assistant as renter.

## Stop at the end
Press Ctrl+C in both server terminals, or run the stop script above.
If you started the servers through Codex, use the stop script before starting your own terminal instances.
Do not delete backend/data to resolve a lock; stop the duplicate backend.