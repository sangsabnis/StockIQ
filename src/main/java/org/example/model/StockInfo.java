package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Model class representing stock information
 */
public class StockInfo {

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("change")
  private BigDecimal change;

  @JsonProperty("changePercent")
  private BigDecimal changePercent;

  @JsonProperty("volume")
  private Long volume;

  @JsonProperty("previousClose")
  private BigDecimal previousClose;

  @JsonProperty("open")
  private BigDecimal open;

  @JsonProperty("high")
  private BigDecimal high;

  @JsonProperty("low")
  private BigDecimal low;

  // Default constructor
  public StockInfo() {}

  // Constructor for basic quote data
  public StockInfo(String symbol, BigDecimal price) {
    this.symbol = symbol;
    this.price = price;
  }

  // Getters and Setters
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getChange() {
    return change;
  }

  public void setChange(BigDecimal change) {
    this.change = change;
  }

  public BigDecimal getChangePercent() {
    return changePercent;
  }

  public void setChangePercent(BigDecimal changePercent) {
    this.changePercent = changePercent;
  }

  public Long getVolume() {
    return volume;
  }

  public void setVolume(Long volume) {
    this.volume = volume;
  }

  public BigDecimal getPreviousClose() {
    return previousClose;
  }

  public void setPreviousClose(BigDecimal previousClose) {
    this.previousClose = previousClose;
  }

  public BigDecimal getOpen() {
    return open;
  }

  public void setOpen(BigDecimal open) {
    this.open = open;
  }

  public BigDecimal getHigh() {
    return high;
  }

  public void setHigh(BigDecimal high) {
    this.high = high;
  }

  public BigDecimal getLow() {
    return low;
  }

  public void setLow(BigDecimal low) {
    this.low = low;
  }

  @Override
  public String toString() {
    return "StockInfo{" +
        "symbol='" + symbol + '\'' +
        ", price=" + price +
        ", change=" + change +
        ", changePercent=" + changePercent +
        ", volume=" + volume +
        '}';
  }
}
