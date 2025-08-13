package org.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.example.model.StockInfo;
import org.example.model.StockOverview;
import org.example.repository.StockOverviewRepository;
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
  private final StockOverviewRepository stockOverviewRepository;

  @Inject
  public AlphaVantageStockService(ObjectMapper objectMapper, StockOverviewRepository stockOverviewRepository) {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.objectMapper = objectMapper;
    this.stockOverviewRepository = stockOverviewRepository;
  }

  @Override
  public StockInfo getStockInfo(String symbol) {
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

  @Override
  public StockOverview getOverview(String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Stock symbol cannot be null or empty");
    }
    
    String normalizedSymbol = symbol.trim().toUpperCase();
    
    // Check if we have fresh data in cache
    var cachedOverview = stockOverviewRepository.findBySymbolIfFresh(normalizedSymbol);
    if (cachedOverview.isPresent()) {
      LOGGER.info("Returning cached stock overview for symbol: {}", normalizedSymbol);
      return cachedOverview.get();
    }
    
    // Fetch fresh data from API
    StockOverview freshOverview = fetchOverviewFromApi(normalizedSymbol);
    
    // Save to cache
    stockOverviewRepository.save(freshOverview);
    LOGGER.info("Cached fresh stock overview for symbol: {}", normalizedSymbol);
    
    return freshOverview;
  }
  
  private StockOverview fetchOverviewFromApi(String symbol) {
    if (API_KEY == null || API_KEY.trim().isEmpty()) {
      LOGGER.warn("Alpha Vantage API key not configured, returning mock data");
      return createMockStockOverview(symbol);
    }

    try {
      String url = String.format("%s?function=OVERVIEW&symbol=%s&apikey=%s",
          BASE_URL, symbol, API_KEY);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(Duration.ofSeconds(10))
          .GET()
          .build();

      LOGGER.info("Fetching stock overview from API for symbol: {}", symbol);

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());
      LOGGER.info("Response {} from stock overview for symbol: {}", response, symbol);

      if (response.statusCode() != 200) {
        LOGGER.error("API request failed with status: {}", response.statusCode());
        throw new RuntimeException("Failed to fetch stock overview: HTTP " + response.statusCode());
      }

      return parseOverviewResponse(response.body(), symbol);

    } catch (IOException | InterruptedException e) {
      LOGGER.error("Error fetching stock overview for symbol: {}", symbol, e);
      throw new RuntimeException("Failed to fetch stock overview", e);
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

  private StockOverview parseOverviewResponse(String jsonResponse, String symbol) {
    try {
      JsonNode root = objectMapper.readTree(jsonResponse);

      // Check for API error messages
      JsonNode errorMessage = root.get("Error Message");
      if (errorMessage != null) {
        throw new RuntimeException("API Error: " + errorMessage.asText());
      }

      JsonNode note = root.get("Note");
      if (note != null) {
        LOGGER.warn("API rate limit reached: {}", note.asText());
        return createMockStockOverview(symbol);
      }

      StockOverview overview = new StockOverview();
      overview.setSymbol(getJsonText(root, "Symbol"));
      overview.setName(getJsonText(root, "Name"));
      overview.setExchange(getJsonText(root, "Exchange"));
      overview.setCurrency(getJsonText(root, "Currency"));
      overview.setCountry(getJsonText(root, "Country"));
      overview.setSector(getJsonText(root, "Sector"));
      overview.setIndustry(getJsonText(root, "Industry"));

      // Valuation metrics
      overview.setPeRatio(parseDecimalField(root, "PERatio"));
      overview.setPriceToBookRatio(parseDecimalField(root, "PriceToBookRatio"));
      overview.setPegRatio(parseDecimalField(root, "PEGRatio"));

      // Profitability metrics
      overview.setReturnOnEquityTTM(parseDecimalField(root, "ReturnOnEquityTTM"));
      overview.setReturnOnAssetsTTM(parseDecimalField(root, "ReturnOnAssetsTTM"));
      overview.setProfitMargin(parseDecimalField(root, "ProfitMargin"));
      overview.setOperatingMarginTTM(parseDecimalField(root, "OperatingMarginTTM"));

      // Financial health metrics
      overview.setCurrentRatio(parseDecimalField(root, "CurrentRatio"));
      overview.setDebtToEquityRatio(parseDecimalField(root, "DebtToEquityRatio"));
      overview.setQuickRatio(parseDecimalField(root, "QuickRatio"));

      // Growth metrics
      overview.setQuarterlyRevenueGrowthYOY(parseDecimalField(root, "QuarterlyRevenueGrowthYOY"));
      overview.setQuarterlyEarningsGrowthYOY(parseDecimalField(root, "QuarterlyEarningsGrowthYOY"));

      // Risk metrics
      overview.setBeta(parseDecimalField(root, "Beta"));

      LOGGER.info("Successfully parsed stock overview for: {}", symbol);
      return overview;

    } catch (Exception e) {
      LOGGER.error("Error parsing Alpha Vantage overview response for symbol: {}", symbol, e);
      LOGGER.debug("Response body: {}", jsonResponse);

      // Return mock data as fallback
      return createMockStockOverview(symbol);
    }
  }

  private String getJsonText(JsonNode root, String fieldName) {
    JsonNode node = root.get(fieldName);
    return (node != null && !node.isNull()) ? node.asText() : null;
  }

  private BigDecimal parseDecimalField(JsonNode root, String fieldName) {
    JsonNode node = root.get(fieldName);
    if (node == null || node.isNull() || "None".equals(node.asText()) || node.asText().trim().isEmpty()) {
      return null;
    }
    try {
      return new BigDecimal(node.asText());
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not parse {} as decimal: {}", fieldName, node.asText());
      return null;
    }
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

  private StockOverview createMockStockOverview(String symbol) {
    LOGGER.info("Creating mock stock overview for symbol: {}", symbol);

    StockOverview mockOverview = new StockOverview();
    mockOverview.setSymbol(symbol);
    
    // Generate company name based on symbol
    mockOverview.setName(symbol + " Corporation");
    mockOverview.setExchange("NASDAQ");
    mockOverview.setCurrency("USD");
    mockOverview.setCountry("USA");
    mockOverview.setSector("Technology");
    mockOverview.setIndustry("Software");

    // Generate realistic mock metrics based on symbol hash
    int hashCode = Math.abs(symbol.hashCode());
    
    // Valuation metrics
    mockOverview.setPeRatio(new BigDecimal(15 + (hashCode % 25))); // PE 15-40
    mockOverview.setPriceToBookRatio(new BigDecimal(2 + (hashCode % 8))); // P/B 2-10
    mockOverview.setPegRatio(new BigDecimal("1." + (hashCode % 50))); // PEG 1.0-1.50

    // Profitability metrics (as percentages)
    mockOverview.setReturnOnEquityTTM(new BigDecimal(10 + (hashCode % 20))); // ROE 10-30%
    mockOverview.setReturnOnAssetsTTM(new BigDecimal(10 + (hashCode % 20))); // ROA 10-30%
    mockOverview.setProfitMargin(new BigDecimal(5 + (hashCode % 20))); // Profit margin 5-25%
    mockOverview.setOperatingMarginTTM(new BigDecimal(8 + (hashCode % 17))); // Operating margin 8-25%

    // Financial health metrics
    mockOverview.setCurrentRatio(new BigDecimal("1." + (20 + (hashCode % 80)))); // Current ratio 1.2-2.0
    mockOverview.setDebtToEquityRatio(new BigDecimal("0." + (10 + (hashCode % 90)))); // D/E 0.1-1.0
    mockOverview.setQuickRatio(new BigDecimal("1." + (10 + (hashCode % 70)))); // Quick ratio 1.1-1.8

    // Growth metrics (as percentages)
    mockOverview.setQuarterlyRevenueGrowthYOY(new BigDecimal(5 + (hashCode % 30))); // Revenue growth 5-35%
    mockOverview.setQuarterlyEarningsGrowthYOY(new BigDecimal(3 + (hashCode % 40))); // Earnings growth 3-43%

    // Risk metrics
    mockOverview.setBeta(new BigDecimal("1." + (hashCode % 50))); // Beta 1.0-1.50

    return mockOverview;
  }
}