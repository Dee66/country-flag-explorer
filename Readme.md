# Country Flags API

A Spring Boot REST API for managing and viewing country flag data.

---

## Prerequisites

- Java 21 (recommended: Temurin distribution)
- Maven 3.8+ (The Maven wrapper `./mvnw` is available)
- Internet connection for first-time data loading (from restcountries.com)

---

## Setup and Running

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Dee66/country-flag-explorer.git
   cd flags
   ```

2. **Build and run the application:**
   ```sh
   ./mvnw clean spring-boot:run
   ```
   The application will start on [http://localhost:8080](http://localhost:8080).

3. **First run:**  
   - Country data is fetched from the [REST Countries API](https://restcountries.com/v3.1/all) and stored locally.

---

## API Documentation

Interactive REST API documentation is available via Swagger UI:

[Swagger UI - API Docs](http://localhost:8080/swagger-ui/index.html)

You can use this link to explore and test all available endpoints in your browser.