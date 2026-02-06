package com.facsciencesuy1.planning_management.planning_service.utils.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Planning Service API") // Change per service
						.version("1.0.0")
						.description("Course scheduling and timetable management") // Change per
																					// service
						.contact(new Contact()
								.name("Planning Team")
								.email("planning@example.com"))
						.license(new License()
								.name("GNU GPL 3.0")
								.url("https://www.gnu.org/licenses/gpl-3.0.html")));
	}
}