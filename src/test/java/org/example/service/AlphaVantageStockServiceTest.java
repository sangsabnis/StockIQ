package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.model.StockInfo;
import org.example.model.StockOverview;
import org.example.service.impl.AlphaVantageStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AlphaVantageStockService
 */
class AlphaVantageStockServiceTest {

  private StockService stockService;

  @BeforeEach
  void setUp() {
    stockService = new AlphaVantageStockService();
  }

  @Test
  void testGetStockInfoWithValidSymbol() {
    // Since we don't have API key in test environment, this will return mock data
    StockInfo result = stockService.getStockInfo("AAPL");

    assertNotNull(result);
    assertEquals("AAPL", result.getSymbol());
    assertNotNull(result.getPrice());
    assertTrue(result.getPrice().doubleValue() > 0);
  }

  @Test
  void testGetStockInfoWithDifferentSymbols() {
    StockInfo appleStock = stockService.getStockInfo("AAPL");
    StockInfo microsoftStock = stockService.getStockInfo("MSFT");

    assertNotNull(appleStock);
    assertNotNull(microsoftStock);
    assertEquals("AAPL", appleStock.getSymbol());
    assertEquals("MSFT", microsoftStock.getSymbol());

    // Mock data should be different for different symbols
    assertNotEquals(appleStock.getPrice(), microsoftStock.getPrice());
  }

  @Test
  void testGetStockInfoWithLowerCaseSymbol() {
    StockInfo result = stockService.getStockInfo("aapl");

    assertNotNull(result);
    assertEquals("AAPL", result.getSymbol()); // Should be converted to uppercase
  }

  @Test
  void testGetStockInfoWithEmptySymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getStockInfo("");
    });
  }

  @Test
  void testGetStockInfoWithNullSymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getStockInfo(null);
    });
  }

  @Test
  void testGetStockInfoWithWhitespaceSymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getStockInfo("   ");
    });
  }

  @Test
  void testMockDataConsistency() {
    // Mock data should be consistent for the same symbol
    StockInfo first = stockService.getStockInfo("TEST");
    StockInfo second = stockService.getStockInfo("TEST");

    assertEquals(first.getSymbol(), second.getSymbol());
    assertEquals(first.getPrice(), second.getPrice());
    assertEquals(first.getVolume(), second.getVolume());
  }

  @Test
  void testGetOverviewWithValidSymbol() {
    // Since we don't have API key in test environment, this will return mock data
    StockOverview result = stockService.getOverview("AAPL");

    assertNotNull(result);
    assertEquals("AAPL", result.getSymbol());
    assertNotNull(result.getName());
    assertNotNull(result.getCategorizedMetrics());
  }

  @Test
  void testGetOverviewWithDifferentSymbols() {
    StockOverview appleOverview = stockService.getOverview("AAPL");
    StockOverview microsoftOverview = stockService.getOverview("MSFT");

    assertNotNull(appleOverview);
    assertNotNull(microsoftOverview);
    assertEquals("AAPL", appleOverview.getSymbol());
    assertEquals("MSFT", microsoftOverview.getSymbol());

    // Mock data should be different for different symbols
    assertNotEquals(appleOverview.getPeRatio(), microsoftOverview.getPeRatio());
    assertNotEquals(appleOverview.getBeta(), microsoftOverview.getBeta());
  }

  @Test
  void testGetOverviewWithLowerCaseSymbol() {
    StockOverview result = stockService.getOverview("aapl");

    assertNotNull(result);
    assertEquals("AAPL", result.getSymbol()); // Should be converted to uppercase
  }

  @Test
  void testGetOverviewWithEmptySymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getOverview("");
    });
  }

  @Test
  void testGetOverviewWithNullSymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getOverview(null);
    });
  }

  @Test
  void testGetOverviewWithWhitespaceSymbol() {
    assertThrows(IllegalArgumentException.class, () -> {
      stockService.getOverview("   ");
    });
  }

  @Test
  void testOverviewMockDataConsistency() {
    // Mock data should be consistent for the same symbol
    StockOverview first = stockService.getOverview("TEST");
    StockOverview second = stockService.getOverview("TEST");

    assertEquals(first.getSymbol(), second.getSymbol());
    assertEquals(first.getPeRatio(), second.getPeRatio());
    assertEquals(first.getBeta(), second.getBeta());
    assertEquals(first.getCurrentRatio(), second.getCurrentRatio());
  }

  @Test
  void testOverviewCategorizedMetrics() {
    StockOverview overview = stockService.getOverview("GOOGL");
    
    assertNotNull(overview.getCategorizedMetrics());
    assertTrue(overview.getCategorizedMetrics().containsKey("valuation"));
    assertTrue(overview.getCategorizedMetrics().containsKey("profitability"));
    assertTrue(overview.getCategorizedMetrics().containsKey("financial_health"));
    assertTrue(overview.getCategorizedMetrics().containsKey("growth"));
    assertTrue(overview.getCategorizedMetrics().containsKey("risk"));

    // Check valuation metrics
    var valuation = overview.getCategorizedMetrics().get("valuation");
    assertTrue(valuation.containsKey("PERatio"));
    assertTrue(valuation.containsKey("PriceToBookRatio"));
    assertTrue(valuation.containsKey("PEGRatio"));

    // Check profitability metrics
    var profitability = overview.getCategorizedMetrics().get("profitability");
    assertTrue(profitability.containsKey("ReturnOnEquityTTM"));
    assertTrue(profitability.containsKey("ProfitMargin"));
    assertTrue(profitability.containsKey("OperatingMarginTTM"));

    // Check financial health metrics
    var financialHealth = overview.getCategorizedMetrics().get("financial_health");
    assertTrue(financialHealth.containsKey("CurrentRatio"));
    assertTrue(financialHealth.containsKey("DebtToEquityRatio"));
    assertTrue(financialHealth.containsKey("QuickRatio"));

    // Check growth metrics
    var growth = overview.getCategorizedMetrics().get("growth");
    assertTrue(growth.containsKey("QuarterlyRevenueGrowthYOY"));
    assertTrue(growth.containsKey("QuarterlyEarningsGrowthYOY"));

    // Check risk metrics
    var risk = overview.getCategorizedMetrics().get("risk");
    assertTrue(risk.containsKey("Beta"));
  }
}