package org.example.service;
import org.example.model.StockInfo;
import org.example.model.StockOverview;

/**
 * Service interface for stock market data
 */
public interface StockService {
  StockInfo getStockInfo(String symbol);
  StockOverview getOverview(String symbol);
}