package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Planning Management - API Gateway")
						.version("26.02.0")
						.contact(new Contact()
								.name("API Support")
								.email("gedeontigadev@gmail.com"))
						.license(new License()
								.name("GNU GPL 3.0")
								.url("https://fsf.org/")))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("bearer-jwt", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("Enter JWT token from /api/auth/login endpoint. Format: Bearer <token>")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}