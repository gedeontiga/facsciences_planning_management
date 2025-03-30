package com.facsciences_planning_management.facsciences_planning_management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.models.RoleType;

@SpringBootApplication
public class FacsciencesPlanningManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacsciencesPlanningManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner start(RoleRepository roleRepository) throws Exception {
		return args -> {
			for (RoleType roleType : RoleType.values()) {
				roleRepository.save(roleRepository.findByType(roleType).orElse(new Role(roleType)));
			}
		};
	}

}
