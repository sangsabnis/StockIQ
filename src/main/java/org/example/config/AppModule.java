package org.example.config;

import org.example.service.HelloService;
import org.example.service.impl.HelloServiceImpl;
import com.google.inject.AbstractModule;

/**
 * Guice module for dependency injection configuration
 */
public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    // Bind service interfaces to their implementations
    bind(HelloService.class).to(HelloServiceImpl.class);

    // Additional bindings can be added here
    // For example:
    // bind(SomeInterface.class).to(SomeImplementation.class);
    // bind(AnotherInterface.class).toInstance(new AnotherImplementation());
  }
}
