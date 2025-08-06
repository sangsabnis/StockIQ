package org.example.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import org.example.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * Controller for handling hello requests
 */
@Singleton
public class HelloController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

  private final HelloService helloService;

  @Inject
  public HelloController(HelloService helloService) {
    this.helloService = helloService;
  }

  public void getHello(Context ctx) {
    String name = ctx.queryParam("name");
    if (name == null || name.trim().isEmpty()) {
      name = "World";
    }

    LOGGER.info("Received hello request for name: {}", name);
    
    String greeting = helloService.getGreeting(name);
    ctx.json(Map.of("message", greeting));
  }
}