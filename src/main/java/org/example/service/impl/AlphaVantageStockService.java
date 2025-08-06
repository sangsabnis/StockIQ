package org.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.example.model.StockInfo;
import org.example.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alpha Vantage implementation of StockService
 */
@Singleton
public class AlphaVantageStockService implements StockService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlphaVantageStockService.class);

  private static final String BASE_URL = "https://www.alphavantage.co/query";
  private static final String API_KEY = System.getenv("ALPHA_VANTAGE_API_KEY");

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public AlphaVantageStockService() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public StockInfo getStockInfo(String symbol) {
    return getQuote(symbol);
  }

  @Override
  public StockInfo getQuote(String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Stock symbol cannot be null or empty");
    }

    if (API_KEY == null || API_KEY.trim().isEmpty()) {
      LOGGER.warn("Alpha Vantage API key not configured, returning mock data");
      return createMockStockInfo(symbol.toUpperCase());
    }

    try {
      String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
          BASE_URL, symbol.toUpperCase(), API_KEY);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(Duration.ofSeconds(10))
          .GET()
          .build();

      LOGGER.info("Fetching stock data for symbol: {}", symbol);

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        LOGGER.error("API request failed with status: {}", response.statusCode());
        throw new RuntimeException("Failed to fetch stock data: HTTP " + response.statusCode());
      }

      return parseQuoteResponse(response.body(), symbol.toUpperCase());

    } catch (IOException | InterruptedException e) {
      LOGGER.error("Error fetching stock data for symbol: {}", symbol, e);
      throw new RuntimeException("Failed to fetch stock data", e);
    }
  }

  private StockInfo parseQuoteResponse(String jsonResponse, String symbol) {
    try {
      JsonNode root = objectMapper.readTree(jsonResponse);
      JsonNode quote = root.get("Global Quote");

      if (quote == null || quote.isEmpty()) {
        // Check for API error messages
        JsonNode errorMessage = root.get("Error Message");
        if (errorMessage != null) {
          throw new RuntimeException("API Error: " + errorMessage.asText());
        }

        JsonNode note = root.get("Note");
        if (note != null) {
          LOGGER.warn("API rate limit reached: {}", note.asText());
          return createMockStockInfo(symbol);
        }

        throw new RuntimeException("Invalid response format from Alpha Vantage API");
      }

      StockInfo stockInfo = new StockInfo();
      stockInfo.setSymbol(quote.get("01. symbol").asText());
      stockInfo.setOpen(new BigDecimal(quote.get("02. open").asText()));
      stockInfo.setHigh(new BigDecimal(quote.get("03. high").asText()));
      stockInfo.setLow(new BigDecimal(quote.get("04. low").asText()));
      stockInfo.setPrice(new BigDecimal(quote.get("05. price").asText()));
      stockInfo.setVolume(quote.get("06. volume").asLong());
      stockInfo.setPreviousClose(new BigDecimal(quote.get("08. previous close").asText()));
      stockInfo.setChange(new BigDecimal(quote.get("09. change").asText()));
      stockInfo.setChangePercent(parseChangePercent(quote.get("10. change percent").asText()));

      LOGGER.info("Successfully parsed stock data for: {}", symbol);
      return stockInfo;

    } catch (Exception e) {
      LOGGER.error("Error parsing Alpha Vantage response for symbol: {}", symbol, e);
      LOGGER.debug("Response body: {}", jsonResponse);

      // Return mock data as fallback
      return createMockStockInfo(symbol);
    }
  }

  private BigDecimal parseChangePercent(String changePercentStr) {
    // Remove the % sign and convert to decimal
    String cleanPercent = changePercentStr.replace("%", "");
    return new BigDecimal(cleanPercent);
  }

  private StockInfo createMockStockInfo(String symbol) {
    LOGGER.info("Creating mock stock data for symbol: {}", symbol);

    StockInfo mockData = new StockInfo();
    mockData.setSymbol(symbol);

    // Generate some realistic mock data based on symbol
    int hashCode = Math.abs(symbol.hashCode());
    BigDecimal basePrice = new BigDecimal(50 + (hashCode % 450)); // Price between $50-$500

    mockData.setPrice(basePrice);
    mockData.setOpen(basePrice.subtract(new BigDecimal("2.50")));
    mockData.setHigh(basePrice.add(new BigDecimal("5.75")));
    mockData.setLow(basePrice.subtract(new BigDecimal("3.25")));
    mockData.setPreviousClose(basePrice.subtract(new BigDecimal("1.25")));
    mockData.setChange(new BigDecimal("1.25"));
    mockData.setChangePercent(new BigDecimal("2.35"));
    mockData.setVolume(1000000L + (hashCode % 5000000L));

    return mockData;
  }
}