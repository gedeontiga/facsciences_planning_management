package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Planning Management API Gateway")
						.version("1.0.0")
						.description("Unified API Gateway for microservices architecture")
						.contact(new Contact()
								.name("API Support")
								.email("support@example.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(
						new Server()
								.url("http://localhost:8080")
								.description("Local Gateway"),
						new Server()
								.url("https://api.example.com")
								.description("Production Gateway")))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("bearer-jwt", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("JWT token authentication")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}