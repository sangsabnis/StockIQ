# Java Guice Starter Project

A simple Java web application demonstrating dependency injection with Google Guice, built with Maven.

## Features

- **Google Guice** for dependency injection
- **Maven** for build management
- **Jetty** embedded web server
- **Jackson** for JSON processing
- **SLF4J** for logging
- **JUnit 5** for testing
- RESTful "Hello World" endpoint
- Health check endpoint

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/example/
│           ├── Application.java              # Main class
│           ├── config/
│           │   └── AppModule.java           # Guice DI configuration
│           ├── service/
│           │   ├── HelloService.java        # Service interface
│           │   └── impl/
│           │       └── HelloServiceImpl.java # Service implementation
│           └── servlet/
│               └── HelloServlet.java        # HTTP endpoint
└── test/
    └── java/
        └── com/example/service/impl/
            └── HelloServiceTest.java         # Unit tests
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone or create the project structure** with the provided files

2. **Build the project:**
   ```bash
   mvn clean compile
   ```

3. **Run tests:**
   ```bash
   mvn test
   ```

4. **Start the application:**
   ```bash
   mvn exec:java
   ```

   The server will start on port 8080 by default.

### Testing the Endpoints

1. **Hello World endpoint:**
   ```bash
   curl http://localhost:8080/hello
   ```
   Response:
   ```json
   {
     "message": "Hello, World!",
     "timestamp": 1691234567890,
     "version": "1.0"
   }
   ```

2. **Hello with name parameter:**
   ```bash
   curl "http://localhost:8080/hello?name=Alice"
   ```
   Response:
   ```json
   {
     "message": "Hello, Alice!",
     "timestamp": 1691234567890,
     "version": "1.0"
   }
   ```

3. **Health check endpoint:**
   ```bash
   curl http://localhost:8080/health
   ```
   Response:
   ```json
   {
     "status": "UP",
     "timestamp": 1691234567890
   }
   ```

## Configuration

### Port Configuration

Set the `PORT` environment variable to change the server port:

```bash
export PORT=9000
mvn exec:java
```

### Adding New Dependencies

Add new services and their implementations to the `AppModule.java` file:

```java
@Override
protected void configure() {
    bind(HelloService.class).to(HelloServiceImpl.class);
    bind(YourNewService.class).to(YourNewServiceImpl.class);
}
```

## Development

### Adding New Endpoints

1. Create a new servlet class in `com.example.servlet`
2. Inject required services using `@Inject`
3. Register the servlet in `Application.java`

### Adding New Services

1. Create an interface in `com.example.service`
2. Create an implementation in `com.example.service.impl`
3. Add the binding in `AppModule.java`
4. Write unit tests in the `test` directory

## Maven Commands

- **Clean:** `mvn clean`
- **Compile:** `mvn compile`
- **Test:** `mvn test`
- **Run:** `mvn exec:java`
- **Package:** `mvn package`

## Dependencies

- **Google Guice 7.0.0** - Dependency injection
- **Jetty 11.0.18** - Embedded web server
- **Jackson 2.16.1** - JSON processing
- **SLF4J 2.0.9** - Logging facade
- **JUnit 5.10.1** - Testing framework

## License

This project is a starter template for educational and development purposes.