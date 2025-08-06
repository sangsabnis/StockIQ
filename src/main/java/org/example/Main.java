package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import java.util.Map;
import org.example.config.AppModule;
import org.example.controller.HelloController;
import org.example.controller.StockController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    try {
      // Create Guice injector
      Injector injector = Guice.createInjector(new AppModule());

      // Get port from environment or use default
      int port = getPort();

      // Create and configure Javalin app
      Javalin app = Javalin.create(config -> {
        // Configure Jackson for JSON serialization
        config.jsonMapper(new JavalinJackson());
        
        // Enable CORS for frontend compatibility
        config.bundledPlugins.enableCors(cors -> {
          cors.addRule(it -> {
            it.anyHost();
            it.allowCredentials = false;
          });
        });
      });

      // Get controllers from Guice
      HelloController helloController = injector.getInstance(HelloController.class);
      StockController stockController = injector.getInstance(StockController.class);

      // Define routes
      app.get("/hello", helloController::getHello);

      app.get("/stock-info", stockController::getStock);
      app.post("/stock", stockController::postStock);

      // Health check endpoint
      app.get("/health", ctx -> {
        ctx.json(Map.of(
            "status", "UP", 
            "timestamp", System.currentTimeMillis()
        ));
      });

      // Start server
      app.start(port);
      LOGGER.info("Server started successfully on port {}", port);
      LOGGER.info("Hello World endpoint: http://localhost:{}/hello", port);
      LOGGER.info("Stock endpoint: http://localhost:{}/stock-info", port);
      LOGGER.info("Health check endpoint: http://localhost:{}/health", port);
      LOGGER.info("Try: http://localhost:{}/hello?name=YourName", port);

    } catch (Exception e) {
      LOGGER.error("Failed to start application", e);
      System.exit(1);
    }
  }

  private static int getPort() {
    String portStr = System.getenv("PORT");
    if (portStr != null && !portStr.trim().isEmpty()) {
      try {
        return Integer.parseInt(portStr.trim());
      } catch (NumberFormatException e) {
        LOGGER.warn("Invalid PORT environment variable: {}. Using default port {}",
            portStr, DEFAULT_PORT);
      }
    }
    return DEFAULT_PORT;
  }

}