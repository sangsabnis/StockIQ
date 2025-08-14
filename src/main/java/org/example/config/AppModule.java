package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.example.model.StockOverview;
import org.example.repository.StockOverviewRowMapper;
import org.example.service.HelloService;
import org.example.service.StockService;
import org.example.service.impl.AlphaVantageStockService;
import org.example.service.impl.HelloServiceImpl;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

/**
 * Guice module for dependency injection configuration
 */
public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    // Bind service interfaces to their implementations
    bind(HelloService.class).to(HelloServiceImpl.class);
    bind(StockService.class).to(AlphaVantageStockService.class);
  }
  
  @Provides
  @Singleton
  public DataSource provideDataSource() {
    return DatabaseConfig.getDataSource();
  }
  
  @Provides
  @Singleton
  public ObjectMapper provideObjectMapper() {
    return new ObjectMapper();
  }
  
  @Provides
  @Singleton
  public Jdbi provideJdbi(ObjectMapper objectMapper) {
    return DatabaseConfig.getJdbi()
            .registerRowMapper(StockOverview.class, new StockOverviewRowMapper(objectMapper));
  }
}
