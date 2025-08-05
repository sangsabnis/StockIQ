package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.example.model.StockInfo;
import org.example.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for handling stock data requests
 */
@Singleton
public class StockServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory.getLogger(StockServlet.class);

  private final StockService stockService;
  private final ObjectMapper objectMapper;

  @Inject
  public StockServlet(StockService stockService) {
    this.stockService = stockService;
    this.objectMapper = new ObjectMapper();
//    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    String symbol = req.getParameter("symbol");

    if (symbol == null || symbol.trim().isEmpty()) {
      sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
          "Missing required parameter: symbol");
      return;
    }

    logger.info("Received stock data request for symbol: {}", symbol);

    try {
      StockInfo stockInfo = stockService.getStockInfo(symbol.trim().toUpperCase());

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("data", stockInfo);

      sendJsonResponse(resp, HttpServletResponse.SC_OK, response);
      logger.info("Successfully returned stock data for: {}", symbol);

    } catch (IllegalArgumentException e) {
      logger.warn("Invalid request for symbol: {}, error: {}", symbol, e.getMessage());
      sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

    } catch (Exception e) {
      logger.error("Error fetching stock data for symbol: {}", symbol, e);
      sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "Failed to fetch stock data. Please try again later.");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    // For future batch requests
    sendErrorResponse(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
        "POST method not yet implemented. Use GET with ?symbol=TICKER");
  }

  private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data)
      throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setStatus(statusCode);

    // Add CORS headers for frontend compatibility
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

    objectMapper.writeValue(resp.getWriter(), data);
  }

  private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message)
      throws IOException {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("success", false);
    errorResponse.put("error", message);
    errorResponse.put("timestamp", System.currentTimeMillis());

    sendJsonResponse(resp, statusCode, errorResponse);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
    // Handle CORS preflight requests
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}