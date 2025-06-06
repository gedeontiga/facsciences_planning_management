package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.components;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.RoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthDataProvider {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DOMAIN_NAME = "@facsciences-uy1.cm";
    private static final String DEFAULT_PASSWORD_SUFFIX = ".password123!";
    private static final String DEFAULT_ADDRESS = "UY1";
    private static final String DEFAULT_PHONE = "000000000";

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialData() {
        try {
            log.info("Starting authentication data loading...");

            loadRoles();
            loadUsersFromJson();

            log.info("Authentication data loading completed successfully");

        } catch (Exception e) {
            log.error("Failed to load initial authentication data", e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private void loadRoles() {
        log.info("Loading roles...");

        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (!roleRepository.existsByType(roleType)) {
                Role role = new Role(roleType);
                roleRepository.save(role);
                log.debug("Created role: {}", roleType);
            }
        });

        log.info("Roles loading completed");
    }

    private void loadUsersFromJson() throws IOException {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping user initialization");
            return;
        }

        log.info("Loading users from data sources...");

        loadDefaultAdmins();
        loadTeachersFromSubjectData();

        log.info("Users loading completed");
    }

    private void loadDefaultAdmins() {
        log.info("Loading default administrators...");

        Role adminRole = roleRepository.findByType(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        List<String> adminUsernames = List.of("Gedeon Ambomo", "Beral Assonfack");

        adminUsernames.forEach(username -> {
            try {
                createAdminUser(username, adminRole);
            } catch (Exception e) {
                log.warn("Failed to create admin user: {}", username, e);
            }
        });

        log.info("Default administrators loading completed");
    }

    private void createAdminUser(String username, Role adminRole) {
        String[] nameParts = parseFullName(username);
        String firstName = nameParts[0];
        String lastName = nameParts[1];
        String email = generateEmailFromName(username) + DOMAIN_NAME;

        if (userRepository.existsByEmail(email)) {
            log.debug("Admin user already exists: {}", email);
            return;
        }

        Users admin = Users.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(DEFAULT_ADDRESS)
                .phoneNumber(DEFAULT_PHONE)
                .email(email)
                .enabled(true)
                .password(passwordEncoder.encode(firstName.toLowerCase() + DEFAULT_PASSWORD_SUFFIX))
                .role(adminRole)
                .build();

        userRepository.save(admin);
        log.debug("Created admin user: {}", email);
    }

    private void loadTeachersFromSubjectData() throws IOException {
        log.info("Loading teachers from subject data...");

        Resource resource = new ClassPathResource("data/subjects.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

        Role teacherRole = roleRepository.findByType(RoleType.TEACHER)
                .orElseThrow(() -> new RuntimeException("Teacher role not found"));

        Set<String> teacherNames = extractTeacherNames(rootNode);

        log.info("Found {} unique teacher names", teacherNames.size());

        createTeacherUsers(teacherNames, teacherRole);

        log.info("Teachers loading completed");
    }

    private Set<String> extractTeacherNames(JsonNode rootNode) {
        Set<String> teacherNames = new HashSet<>();

        JsonNode niveauNode = rootNode.get("niveau");
        if (niveauNode == null) {
            log.warn("No 'niveau' node found in subjects data");
            return teacherNames;
        }

        niveauNode.fieldNames().forEachRemaining(levelCode -> {
            try {
                JsonNode levelNode = niveauNode.get(levelCode);
                processLevelForTeachers(levelNode, teacherNames);
            } catch (Exception e) {
                log.warn("Error processing level: {}", levelCode, e);
            }
        });

        // Remove empty names and invalid entries
        teacherNames.removeIf(name -> name == null || name.trim().isEmpty() || name.equalsIgnoreCase("null"));

        return teacherNames;
    }

    private void processLevelForTeachers(JsonNode levelNode, Set<String> teacherNames) {
        levelNode.fieldNames().forEachRemaining(semester -> {
            try {
                JsonNode semesterNode = levelNode.get(semester);
                JsonNode subjectsArray = semesterNode.get("subjects");

                if (subjectsArray != null && subjectsArray.isArray()) {
                    processSubjectsForTeachers(subjectsArray, teacherNames);
                }
            } catch (Exception e) {
                log.warn("Error processing semester: {}", semester, e);
            }
        });
    }

    private void processSubjectsForTeachers(JsonNode subjectsArray, Set<String> teacherNames) {
        subjectsArray.forEach(subjectNode -> {
            try {
                // Extract Course Lecturer(s)
                extractTeachersFromField(subjectNode, "Course Lecturer", teacherNames);

                // Extract Assistant Lecturer(s) - handle various spellings
                extractTeachersFromField(subjectNode, "Assitant lecturer", teacherNames);
                extractTeachersFromField(subjectNode, "Assistant lecturer", teacherNames);

                // Handle numbered assistant lecturers (Assitant lecturer 1, 2, etc.)
                subjectNode.fieldNames().forEachRemaining(fieldName -> {
                    if (fieldName.toLowerCase().matches(".*assit?ant lecturer.*")) {
                        extractTeachersFromField(subjectNode, fieldName, teacherNames);
                    }
                });

            } catch (Exception e) {
                log.warn("Error processing subject for teachers: {}", subjectNode.get("code"), e);
            }
        });
    }

    private void extractTeachersFromField(JsonNode subjectNode, String fieldName, Set<String> teacherNames) {
        JsonNode fieldNode = subjectNode.get(fieldName);

        if (fieldNode == null || fieldNode.isNull()) {
            return;
        }

        if (fieldNode.isTextual()) {
            String teacherName = fieldNode.asText().trim();
            if (!teacherName.isEmpty() && !teacherName.equalsIgnoreCase("null")) {
                teacherNames.add(teacherName);
            }
        } else if (fieldNode.isArray()) {
            fieldNode.forEach(teacherElement -> {
                String teacherName = teacherElement.asText().trim();
                if (!teacherName.isEmpty() && !teacherName.equalsIgnoreCase("null")) {
                    teacherNames.add(teacherName);
                }
            });
        }
    }

    private void createTeacherUsers(Set<String> teacherNames, Role teacherRole) {
        int created = 0;
        int skipped = 0;

        for (String fullName : teacherNames) {
            try {
                if (createTeacherUser(fullName, teacherRole)) {
                    created++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.warn("Failed to create teacher user: {}", fullName, e);
                skipped++;
            }
        }

        log.info("Teacher creation summary - Created: {}, Skipped: {}", created, skipped);
    }

    private boolean createTeacherUser(String fullName, Role teacherRole) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        String[] nameParts = parseFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];
        String email = generateEmailFromName(fullName) + DOMAIN_NAME;

        if (userRepository.existsByEmail(email)) {
            log.debug("Teacher user already exists: {}", email);
            return false;
        }

        Users teacher = Users.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(DEFAULT_ADDRESS)
                .phoneNumber(DEFAULT_PHONE)
                .email(email)
                .enabled(true)
                .password(passwordEncoder.encode(firstName.toLowerCase() + DEFAULT_PASSWORD_SUFFIX))
                .role(teacherRole)
                .build();

        userRepository.save(teacher);
        log.debug("Created teacher user: {} ({})", email, fullName);
        return true;
    }

    private String[] parseFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[] { "Unknown", "User" };
        }

        String[] nameParts = fullName.trim().split("\\s+");

        if (nameParts.length == 0) {
            return new String[] { "Unknown", "User" };
        } else if (nameParts.length == 1) {
            return new String[] { nameParts[0], "" };
        } else {
            String firstName = nameParts[0];
            String lastName = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length));
            return new String[] { firstName, lastName };
        }
    }

    private String generateEmailFromName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "unknown.user";
        }

        return fullName.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "") // Remove special characters
                .replaceAll("\\s+", ".") // Replace spaces with dots
                .trim();
    }
}
