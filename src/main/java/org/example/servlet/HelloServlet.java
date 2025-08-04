package org.example.servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.HelloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for handling hello world requests
 */
@Singleton
public class HelloServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory.getLogger(HelloServlet.class);

  private final HelloService helloService;
  private final ObjectMapper objectMapper;

  @Inject
  public HelloServlet(HelloService helloService) {
    this.helloService = helloService;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    logger.info("Received GET request to /hello");

    String name = req.getParameter("name");
    String greeting = helloService.getGreeting(name);

    // Create response object
    Map<String, Object> response = new HashMap<>();
    response.put("message", greeting);
    response.put("timestamp", System.currentTimeMillis());
    response.put("version", "1.0");

    // Set response headers
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setStatus(HttpServletResponse.SC_OK);

    // Write JSON response
    objectMapper.writeValue(resp.getWriter(), response);

    logger.info("Sent response: {}", greeting);
  }
}