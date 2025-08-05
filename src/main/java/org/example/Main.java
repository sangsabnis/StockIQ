package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.config.AppModule;
import org.example.servlet.HelloServlet;
import org.example.servlet.StockServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class
 */
public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    try {
      // Create Guice injector
      Injector injector = Guice.createInjector(new AppModule());

      // Get port from environment or use default
      int port = getPort();

      // Create and configure Jetty server
      Server server = new Server(port);
      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");
      server.setHandler(context);

      // Add servlet with Guice injection
      HelloServlet helloServlet = injector.getInstance(HelloServlet.class);
      context.addServlet(new ServletHolder(helloServlet), "/hello");

      // Add stock servlet
      StockServlet stockServlet = injector.getInstance(StockServlet.class);
      context.addServlet(new ServletHolder(stockServlet), "/stock");

      // Add health check endpoint
      context.addServlet(new ServletHolder(new HealthServlet()), "/health");

      // Start server
      server.start();
      logger.info("Server started successfully on port {}", port);
      logger.info("Hello World endpoint: http://localhost:{}/hello", port);
      logger.info("Stock endpoint: http://localhost:{}/stock", port);
      logger.info("Health check endpoint: http://localhost:{}/health", port);
      logger.info("Try: http://localhost:{}/hello?name=YourName", port);

      // Wait for server to be stopped
      server.join();

    } catch (Exception e) {
      logger.error("Failed to start application", e);
      System.exit(1);
    }
  }

  private static int getPort() {
    String portStr = System.getenv("PORT");
    if (portStr != null && !portStr.trim().isEmpty()) {
      try {
        return Integer.parseInt(portStr.trim());
      } catch (NumberFormatException e) {
        logger.warn("Invalid PORT environment variable: {}. Using default port {}",
            portStr, DEFAULT_PORT);
      }
    }
    return DEFAULT_PORT;
  }

  /**
   * Simple health check servlet
   */
  private static class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req,
        HttpServletResponse resp)
        throws java.io.IOException {
      resp.setContentType("application/json");
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.getWriter().write("{\"status\":\"UP\",\"timestamp\":" + System.currentTimeMillis() + "}");
    }
  }
}