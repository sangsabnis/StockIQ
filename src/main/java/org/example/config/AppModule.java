package org.example.config;

import com.google.inject.AbstractModule;
import org.example.service.HelloService;
import org.example.service.StockService;
import org.example.service.impl.AlphaVantageStockService;
import org.example.service.impl.HelloServiceImpl;

/**
 * Guice module for dependency injection configuration
 */
public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    // Bind service interfaces to their implementations
    bind(HelloService.class).to(HelloServiceImpl.class);
    bind(StockService.class).to(AlphaVantageStockService.class);

    // Additional bindings can be added here
    // For example:
    // bind(SomeInterface.class).to(SomeImplementation.class);
    // bind(AnotherInterface.class).toInstance(new AnotherImplementation());
  }
}
