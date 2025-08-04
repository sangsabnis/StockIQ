package org.example.service.impl;
import org.example.service.HelloService;
import com.google.inject.Singleton;

/**
 * Implementation of HelloService
 */
@Singleton
public class HelloServiceImpl implements HelloService {

  @Override
  public String getGreeting(String name) {
    if (name == null || name.trim().isEmpty()) {
      return getDefaultGreeting();
    }
    return "Hello, " + name.trim() + "!";
  }

  @Override
  public String getDefaultGreeting() {
    return "Hello, World!";
  }
}