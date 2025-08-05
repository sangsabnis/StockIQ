package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.model.StockInfo;
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
  void testGetQuoteMethodDelegation() {
    StockInfo result = stockService.getQuote("GOOGL");

    assertNotNull(result);
    assertEquals("GOOGL", result.getSymbol());
    assertNotNull(result.getPrice());
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
}