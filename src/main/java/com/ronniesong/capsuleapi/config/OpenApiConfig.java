package com.ronniesong.capsuleapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI capsuleOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Capsule API")
            .version("0.0.1")
            .description("Temporary short-code export/import API backed by Redis.")
            .contact(new Contact().name("Capsule"))
            .license(new License().name("Private")))
        .servers(List.of(new Server()
            .url("http://localhost:8787")
            .description("Local development")));
  }
}
