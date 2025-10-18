package com.facsciencesuy1.planning_management.planning_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.google.ortools.Loader;

@EnableDiscoveryClient
@SpringBootApplication
public class PlanningServiceApplication {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();
		SpringApplication.run(PlanningServiceApplication.class, args);
	}

}
