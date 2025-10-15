package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Timetable UY1 API Gateway", version = "v1", description = "Unified API Gateway for Planning Management System", contact = @Contact(name = "FacSciences UY1", email = "support@facsciences-uy1.cm"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", description = "Enter JWT token obtained from /api/auth/login")
public class OpenApiConfig {

	@Value("${server.port:8080}")
	private String serverPort;

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("bearerAuth",
								new io.swagger.v3.oas.models.security.SecurityScheme()
										.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("JWT token from /api/auth/login")))

				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

				.servers(Arrays.asList(
						new Server()
								.url("https://facsciences-uy1-planning-management-gedeontiga-eabfb5d3.koyeb.app")
								.description("Production Server (Koyeb)"),
						new Server()
								.url("http://localhost:" + serverPort)
								.description("Local Development Server")));
	}
}