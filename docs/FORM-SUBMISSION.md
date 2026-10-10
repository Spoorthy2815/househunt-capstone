# HouseHunt — Form Submission Details

Form: https://forms.gle/7NG17v45rpPFcEvJ6

## Student fields
- Roll number: [Your roll number]
- Student name: [Your full name]
- College email: [Your college email address]
- Branch: [Choose your actual branch: CSE, AIML, AI, IT, CS, DS]
- Section: [Choose your actual section]
- Trainer: [Choose your actual trainer: Mr P Upendra Varma, Mr G Venu, Mr Harshith Rana, Mr Ram Swaroop, Mr Vineeth]

## Project title
HouseHunt: AI-Assisted House Rental Management System

## About the project — ready to paste
HouseHunt is a full-stack web application that simplifies finding, listing, and managing rental properties. The frontend uses React, Vite, Bootstrap, React Router, and Axios, while the backend uses Java 21, Spring Boot, Spring Security, and Spring Data JPA. The project supports MySQL, with a persistent H2 database for convenient local demonstrations.

Users can register and log in securely using BCrypt password hashing and JWT authentication. Renters can browse approved properties, filter by location, property type, and budget, save favorite homes, view property details and virtual-tour links, and request rentals for selected dates. Property owners can create, edit, and delete eligible listings and accept or decline rental requests. Administrators review listings before publication. Booking validation prevents overlapping accepted reservations.

An AI assistant retrieves approved property information using local feature-hashing embeddings and cosine similarity. It supports optional OpenAI-compatible LLM integration and stores user-specific conversation history. Additional features include validation, exception handling, pagination, sorting, caching, asynchronous processing, structured logging, Actuator monitoring, Swagger documentation, automated tests, and Docker configuration.

## Required links — still pending
- Frontend GitHub link: [Upload project to GitHub, then copy the frontend folder URL]
- Backend GitHub link: [Upload project to GitHub, then copy the backend folder URL]
- Final deployment link: [Deploy both services and a persistent database, then copy the public frontend URL]

A single repository is sufficient for both source links. After upload, each URL can point to the corresponding frontend or backend folder in that repository. Copy real URLs from GitHub; do not submit placeholder URLs.

The current localhost and 192.168.x.x addresses are local development URLs, not publicly accessible deployment links.

## Verification
Five backend tests and four frontend tests passed. The React production build, backend packaging, and desktop/mobile browser smoke checks passed. Docker/MySQL execution and live LLM responses have not been verified in this environment.