package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing stock overview/fundamental data from Alpha Vantage
 */
public class StockOverview {

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("name")
  private String name;

  @JsonProperty("exchange")
  private String exchange;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("country")
  private String country;

  @JsonProperty("sector")
  private String sector;

  @JsonProperty("industry")
  private String industry;

  // Valuation metrics
  @JsonProperty("PERatio")
  private BigDecimal peRatio;

  @JsonProperty("PriceToBookRatio") 
  private BigDecimal priceToBookRatio;

  @JsonProperty("PEGRatio")
  private BigDecimal pegRatio;

  // Profitability metrics
  @JsonProperty("ReturnOnEquityTTM")
  private BigDecimal returnOnEquityTTM;

  @JsonProperty("ReturnOnAssetsTTM")
  private BigDecimal returnOnAssetsTTM;

  @JsonProperty("ProfitMargin")
  private BigDecimal profitMargin;

  @JsonProperty("OperatingMarginTTM")
  private BigDecimal operatingMarginTTM;

  // Financial health metrics
  @JsonProperty("CurrentRatio")
  private BigDecimal currentRatio;

  @JsonProperty("DebtToEquityRatio")
  private BigDecimal debtToEquityRatio;

  @JsonProperty("QuickRatio")
  private BigDecimal quickRatio;

  // Growth metrics
  @JsonProperty("QuarterlyRevenueGrowthYOY")
  private BigDecimal quarterlyRevenueGrowthYOY;

  @JsonProperty("QuarterlyEarningsGrowthYOY")
  private BigDecimal quarterlyEarningsGrowthYOY;

  // Risk metrics
  @JsonProperty("Beta")
  private BigDecimal beta;

  // Default constructor
  public StockOverview() {}

  // Constructor
  public StockOverview(String symbol, String name) {
    this.symbol = symbol;
    this.name = name;
  }

  /**
   * Get categorized metrics as specified in the requirements
   */
  @JsonProperty("metrics")
  public Map<String, Map<String, Object>> getCategorizedMetrics() {
    Map<String, Map<String, Object>> metrics = new HashMap<>();
    
    Map<String, Object> valuation = new HashMap<>();
    valuation.put("PERatio", peRatio);
    valuation.put("PriceToBookRatio", priceToBookRatio);
    valuation.put("PEGRatio", pegRatio);
    metrics.put("valuation", valuation);

    Map<String, Object> profitability = new HashMap<>();
    profitability.put("ReturnOnEquityTTM", returnOnEquityTTM);
    profitability.put("ReturnOnAssetsTTM", returnOnAssetsTTM);
    profitability.put("ProfitMargin", profitMargin);
    profitability.put("OperatingMarginTTM", operatingMarginTTM);
    metrics.put("profitability", profitability);

    Map<String, Object> financial_health = new HashMap<>();
    financial_health.put("CurrentRatio", currentRatio);
    financial_health.put("DebtToEquityRatio", debtToEquityRatio);
    financial_health.put("QuickRatio", quickRatio);
    metrics.put("financial_health", financial_health);

    Map<String, Object> growth = new HashMap<>();
    growth.put("QuarterlyRevenueGrowthYOY", quarterlyRevenueGrowthYOY);
    growth.put("QuarterlyEarningsGrowthYOY", quarterlyEarningsGrowthYOY);
    metrics.put("growth", growth);

    Map<String, Object> risk = new HashMap<>();
    risk.put("Beta", beta);
    metrics.put("risk", risk);

    return metrics;
  }

  // Getters and Setters
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getSector() {
    return sector;
  }

  public void setSector(String sector) {
    this.sector = sector;
  }

  public String getIndustry() {
    return industry;
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public BigDecimal getPeRatio() {
    return peRatio;
  }

  public void setPeRatio(BigDecimal peRatio) {
    this.peRatio = peRatio;
  }

  public BigDecimal getPriceToBookRatio() {
    return priceToBookRatio;
  }

  public void setPriceToBookRatio(BigDecimal priceToBookRatio) {
    this.priceToBookRatio = priceToBookRatio;
  }

  public BigDecimal getPegRatio() {
    return pegRatio;
  }

  public void setPegRatio(BigDecimal pegRatio) {
    this.pegRatio = pegRatio;
  }

  public BigDecimal getReturnOnEquityTTM() {
    return returnOnEquityTTM;
  }

  public void setReturnOnEquityTTM(BigDecimal returnOnEquityTTM) {
    this.returnOnEquityTTM = returnOnEquityTTM;
  }

  public BigDecimal getReturnOnAssetsTTM() {
    return returnOnAssetsTTM;
  }

  public void setReturnOnAssetsTTM(BigDecimal returnOnAssetsTTM) {
    this.returnOnAssetsTTM = returnOnAssetsTTM;
  }

  public BigDecimal getProfitMargin() {
    return profitMargin;
  }

  public void setProfitMargin(BigDecimal profitMargin) {
    this.profitMargin = profitMargin;
  }

  public BigDecimal getOperatingMarginTTM() {
    return operatingMarginTTM;
  }

  public void setOperatingMarginTTM(BigDecimal operatingMarginTTM) {
    this.operatingMarginTTM = operatingMarginTTM;
  }

  public BigDecimal getCurrentRatio() {
    return currentRatio;
  }

  public void setCurrentRatio(BigDecimal currentRatio) {
    this.currentRatio = currentRatio;
  }

  public BigDecimal getDebtToEquityRatio() {
    return debtToEquityRatio;
  }

  public void setDebtToEquityRatio(BigDecimal debtToEquityRatio) {
    this.debtToEquityRatio = debtToEquityRatio;
  }

  public BigDecimal getQuickRatio() {
    return quickRatio;
  }

  public void setQuickRatio(BigDecimal quickRatio) {
    this.quickRatio = quickRatio;
  }

  public BigDecimal getQuarterlyRevenueGrowthYOY() {
    return quarterlyRevenueGrowthYOY;
  }

  public void setQuarterlyRevenueGrowthYOY(BigDecimal quarterlyRevenueGrowthYOY) {
    this.quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY;
  }

  public BigDecimal getQuarterlyEarningsGrowthYOY() {
    return quarterlyEarningsGrowthYOY;
  }

  public void setQuarterlyEarningsGrowthYOY(BigDecimal quarterlyEarningsGrowthYOY) {
    this.quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY;
  }

  public BigDecimal getBeta() {
    return beta;
  }

  public void setBeta(BigDecimal beta) {
    this.beta = beta;
  }

  @Override
  public String toString() {
    return "StockOverview{" +
        "symbol='" + symbol + '\'' +
        ", name='" + name + '\'' +
        ", sector='" + sector + '\'' +
        ", industry='" + industry + '\'' +
        '}';
  }
}