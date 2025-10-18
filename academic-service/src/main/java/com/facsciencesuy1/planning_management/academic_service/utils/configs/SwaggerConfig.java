package com.facsciencesuy1.planning_management.academic_service.utils.configs;

import io.swagger.v3.oas.models.Components;
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
						.title("Academic Service API") // Change per service
						.version("1.0.0")
						.description("Course scheduling and timetable management") // Change per
																					// service
						.contact(new Contact()
								.name("Academic Team")
								.email("academic@example.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(
						new Server()
								.url("http://localhost:8080")
								.description("Direct Service Access (Dev Only)"),
						new Server()
								.url("http://api-gateway:8080") // Gateway port
								.description("API Gateway (Production)")))
				.components(new Components()
						.addSecuritySchemes("gateway-auth", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name("X-Gateway-Secret")
								.description("Gateway authentication")))
				.addSecurityItem(new SecurityRequirement().addList("gateway-auth"));
	}
}