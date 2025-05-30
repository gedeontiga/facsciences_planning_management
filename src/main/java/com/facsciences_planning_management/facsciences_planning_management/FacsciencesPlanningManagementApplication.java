package com.facsciences_planning_management.facsciences_planning_management;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.types.RoleType;

@SpringBootApplication
public class FacsciencesPlanningManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacsciencesPlanningManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) throws Exception {
		// Step 1: Ensure roles are present
		for (RoleType roleType : RoleType.values()) {
			roleRepository.save(roleRepository.findByType(roleType).orElse(new Role(roleType)));
		}

		return args -> {
			Stream<String> adminStream = Stream.of("admin1", "admin2");
			defaultSaveUser(adminStream, userRepository,
					roleRepository.findByType(RoleType.ADMIN).orElseThrow(),
					passwordEncoder);
		};
	}

	public void defaultSaveUser(Stream<String> stream, UserRepository userRepository, Role role,
			PasswordEncoder passwordEncoder) throws IOException {
		final String domainName = "@facsciences-uy1.cm";

		stream.forEach(username -> {
			userRepository.save(userRepository.findByEmail(username + domainName)
					.orElse(
							Users.builder()
									.firstName("ADMIN")
									.address("UY1")
									.phoneNumber("000000000")
									.email(username + domainName)
									.enabled(true)
									.password(passwordEncoder.encode(username + ".password"))
									.role(role)
									.build()));
		});
		userRepository.findAll().forEach(System.out::println);

	}

}
