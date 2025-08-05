package org.example.service;
import org.example.model.StockInfo;

/**
 * Service interface for stock market data
 */
public interface StockService {
  StockInfo getStockInfo(String symbol);
  StockInfo getQuote(String symbol);
}