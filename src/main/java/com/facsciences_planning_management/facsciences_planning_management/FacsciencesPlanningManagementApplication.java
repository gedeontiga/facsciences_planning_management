package com.facsciences_planning_management.facsciences_planning_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.google.ortools.Loader;

@EnableMongoAuditing
@SpringBootApplication
public class FacsciencesPlanningManagementApplication {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();
		SpringApplication.run(FacsciencesPlanningManagementApplication.class, args);
	}
}
