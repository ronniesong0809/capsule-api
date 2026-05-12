package com.ronniesong.capsuleapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "System", description = "Service health")
public class HealthController {
  @Operation(summary = "Health check")
  @GetMapping("/health")
  public Map<String, Boolean> health() {
    return Map.of("ok", true);
  }
}
