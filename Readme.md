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
   git clone https://github.com/your-org/your-repo.git
   cd your-repo
   ```

2. **Build and run the application:**
   ```sh
   ./mvnw clean spring-boot:run
   ```
   The application will start on [http://localhost:8080](http://localhost:8080).

3. **First run:**  
   - If your database is empty, country data is fetched from the [REST Countries API](https://restcountries.com/v3.1/all) and stored locally.

---

## API Usage

- **List all countries**
  ```
  GET /countries
  ```
  Returns:
  ```json
  [
    {"name": "France", "flag": "https://flagcdn.com/fr.svg"},
    ...
  ]
  ```

- **Get country details**
  ```
  GET /countries/{name}
  ```
  Returns:
  ```json
  {"name":"France","flag":"https://flagcdn.com/fr.svg","population":67000000,"capital":"Paris"}
  ```

- **Add a country**
  ```
  POST /countries
  Content-Type: application/json

  {
    "name": "Italy",
    "flag": "it.svg",
    "population": 60000000,
    "capital": "Rome"
  }
  ```

- **Update a country**
  ```
  PUT /countries/{name}
  Content-Type: application/json

  {
    "name": "France",
    "flag": "fr2.svg",
    "population": 68000000,
    "capital": "Paris"
  }
  ```

- **Delete a country**
  ```
  DELETE /countries/{name}
  ```

---

## Running Tests

To run all tests: