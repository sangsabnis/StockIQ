package org.example.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import org.example.model.StockInfo;
import org.example.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling stock data requests
 */
@Singleton
public class StockController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StockController.class);

  private final StockService stockService;

  @Inject
  public StockController(StockService stockService) {
    this.stockService = stockService;
  }

  public void getStock(Context ctx) {
    String symbol = ctx.queryParam("symbol");

    if (symbol == null || symbol.trim().isEmpty()) {
      sendErrorResponse(ctx, HttpStatus.BAD_REQUEST, "Missing required parameter: symbol");
      return;
    }

    LOGGER.info("Received stock data request for symbol: {}", symbol);

    try {
      StockInfo stockInfo = stockService.getStockInfo(symbol.trim().toUpperCase());

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("data", stockInfo);

      ctx.status(HttpStatus.OK).json(response);
      LOGGER.info("Successfully returned stock data for: {}", symbol);

    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invalid request for symbol: {}, error: {}", symbol, e.getMessage());
      sendErrorResponse(ctx, HttpStatus.BAD_REQUEST, e.getMessage());

    } catch (Exception e) {
      LOGGER.error("Error fetching stock data for symbol: {}", symbol, e);
      sendErrorResponse(ctx, HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to fetch stock data. Please try again later.");
    }
  }

  public void postStock(Context ctx) {
    // For future batch requests
    sendErrorResponse(ctx, HttpStatus.METHOD_NOT_ALLOWED,
        "POST method not yet implemented. Use GET with ?symbol=TICKER");
  }

  private void sendErrorResponse(Context ctx, HttpStatus status, String message) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("success", false);
    errorResponse.put("error", message);
    errorResponse.put("timestamp", System.currentTimeMillis());

    ctx.status(status).json(errorResponse);
  }
}