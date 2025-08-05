package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.service.impl.HelloServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for HelloService
 */
class HelloServiceTest {

  private HelloService helloService;

  @BeforeEach
  void setUp() {
    helloService = new HelloServiceImpl();
  }

  @Test
  void testGetGreetingWithValidName() {
    String result = helloService.getGreeting("Alice");
    assertEquals("Hello, Alice!", result);
  }

  @Test
  void testGetGreetingWithEmptyName() {
    String result = helloService.getGreeting("");
    assertEquals("Hello, World!", result);
  }

  @Test
  void testGetGreetingWithNullName() {
    String result = helloService.getGreeting(null);
    assertEquals("Hello, World!", result);
  }

  @Test
  void testGetGreetingWithWhitespaceName() {
    String result = helloService.getGreeting("   ");
    assertEquals("Hello, World!", result);
  }

  @Test
  void testGetGreetingTrimsWhitespace() {
    String result = helloService.getGreeting("  Bob  ");
    assertEquals("Hello, Bob!", result);
  }

  @Test
  void testGetDefaultGreeting() {
    String result = helloService.getDefaultGreeting();
    assertEquals("Hello, World!", result);
  }
}