# Web Crawler (Java + Spring Boot)

This project is a simple **web crawler** built with Java and Spring Boot. Given a starting URL (e.g., `https://example.com`), it recursively traverses all internal links within the same domain and returns a unique list of discovered pages.

---

## Features

- Recursively scans internal links under the same domain.
- Filters out non-navigable links like `mailto:`, `javascript:`, and `#anchors`.
- Prevents infinite loops using a `Set<String>` to store visited pages.
- Separates the HTTP client (`HtmlProvider`) to improve unit testability.

---

## Technologies

- Java 21+
- Spring Boot 3.x
- Jsoup (HTML parsing)
- JUnit 5
- Mockito (for mocking in unit tests)

---

## Project Structure
```
src/main/java/com/ayseakr/webcrawler
├── WebCrawlerService.java    # Core logic for crawling pages
├── HtmlProvider.java         # Responsible for fetching HTML via Jsoup
├── PagesResponse.java        # Response model (domain + pages)
└── WebCrawlerController.java # REST controller
```
```
src/test/java/com/ayseakr/webcrawler
├── WebCrawlerServiceUnitTest.java        # Unit tests with mocked HtmlProvider
└── WebCrawlerServiceIntegrationTest.java # Integration tests for the service
```

## Getting Started

### 1. Clone the repository

```bash
git clone git@github.com:AyseAkr/web-crawler.git
cd web-crawler
```
### 2. Run with Maven

```bash
./mvnw spring-boot:run
```

When application is running, swagger-ui is available at http://localhost:8080/swagger-ui/index.html


### API Example
```
GET /pages?target=https://example.com/
```
### Response
```json
{
  "domain": "https://example.com/",
  "pages": [
    "https://example.com/",
    "https://example.com/services/",
    "https://example.com/about.html"
  ]
}
```
### 3. Run Tests

```bash
./mvnw test
```
