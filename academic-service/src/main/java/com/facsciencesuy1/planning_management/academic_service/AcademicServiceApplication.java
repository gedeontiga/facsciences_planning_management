package com.facsciencesuy1.planning_management.academic_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AcademicServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicServiceApplication.class, args);
	}

}
