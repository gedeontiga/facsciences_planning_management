package com.facsciences_planning_management.facsciences_planning_management.components;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Teacher;
import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.TeacherRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Faculty;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.BranchRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.DepartmentRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.FacultyRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.RoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataProvider {

    private final TeacherRepository teacherRepository;

    private static final String FACULTY_CODE = "FACSCIENCES";
    private static final String FACULTY_NAME = "Faculté Des Sciences";
    private static final String DEPARTMENT_CODE = "DEPT-INFO";
    private static final String DEPARTMENT_NAME = "Département Informatique";
    private static final String BRANCH_CODE = "INFO";
    private static final String BRANCH_NAME = "Informatique";
    private static final String DOMAIN_NAME = "@facsciences-uy1.cm";

    @Value("${app.password-suffix}")
    private String passwordSuffix;
    private static final String DEFAULT_ADDRESS = "UY1";
    private static final String DEFAULT_PHONE = "000000000";

    private final ObjectMapper objectMapper;
    private final RoomRepository roomRepository;
    private final UeRepository ueRepository;

    private final LevelRepository levelRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;

    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialData() {
        try {

            if (isDataAlreadyLoaded()) {
                log.info("Initial data already loaded");
                return;
            }
            Faculty faculty = createOrGetFaculty();
            Branch branch = createOrGetBranch(faculty);
            Department department = createOrGetDepartment(branch);
            loadRoles();
            loadUsersFromJson(department);
            loadRoomsFromJson(faculty);
            loadSubjectsAndRelatedData(faculty, branch);
            log.info("Initial data loading completed successfully");
        } catch (Exception e) {
            log.error("Failed to load initial data", e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private void createCourseFromNode(JsonNode subjectNode, Ue ue) {
        String teacherName = getTextValue(subjectNode, "Course Lecturer");
        if (teacherName == null || teacherName.trim().isEmpty() || teacherName.equalsIgnoreCase("null")) {

            return;
        }

        String email = generateEmailFromName(teacherName) + DOMAIN_NAME;
        Users teacher = userRepository.findByEmail(email)
                .orElseGet(() -> {

                    Role teacherRole = roleRepository.findByType(RoleType.TEACHER)
                            .orElseThrow(() -> new RuntimeException("Teacher role not found"));
                    String[] nameParts = parseFullName(teacherName);
                    Users newTeacher = Users.builder()
                            .firstName(nameParts[0])
                            .lastName(nameParts[1])
                            .address(DEFAULT_ADDRESS)
                            .phoneNumber(DEFAULT_PHONE)
                            .email(email)
                            .enabled(true)
                            .password(passwordEncoder.encode(nameParts[0].toLowerCase() + passwordSuffix))
                            .role(teacherRole)
                            .build();
                    return userRepository.save(newTeacher);
                });

        // Create Course entity
        Course course = Course.builder()
                .ue(ue)
                .teacher(teacher)
                .duration(Duration.ofHours(3))
                .build();

        courseRepository.save(course);

    }

    private void loadRoles() {

        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (!roleRepository.existsByType(roleType)) {
                Role role = new Role(roleType);
                roleRepository.save(role);

            }
        });

    }

    private void loadUsersFromJson(Department department) throws IOException {
        if (userRepository.count() > 0) {

            return;
        }

        loadDefaultAdmins();
        loadTeachersFromSubjectData(department);

    }

    private void loadDefaultAdmins() {

        Role adminRole = roleRepository.findByType(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        List<String> adminUsernames = List.of("Gedeon Ambomo", "Beral Assonfack");

        adminUsernames.forEach(username -> {
            try {
                createAdminUser(username, adminRole);
            } catch (Exception e) {

            }
        });

    }

    private void createAdminUser(String username, Role adminRole) {
        String[] nameParts = parseFullName(username);
        String firstName = nameParts[0];
        String lastName = nameParts[1];
        String email = generateEmailFromName(username) + DOMAIN_NAME;

        if (userRepository.existsByEmail(email)) {

            return;
        }

        Users admin = Users.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(DEFAULT_ADDRESS)
                .phoneNumber(DEFAULT_PHONE)
                .email(email)
                .enabled(true)
                .password(passwordEncoder.encode(firstName.toLowerCase() + passwordSuffix))
                .role(adminRole)
                .build();

        userRepository.save(admin);

    }

    private void loadTeachersFromSubjectData(Department department) throws IOException {

        Resource resource = new ClassPathResource("data/subjects.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

        Role teacherRole = roleRepository.findByType(RoleType.TEACHER)
                .orElseThrow(() -> new RuntimeException("Teacher role not found"));

        Set<String> teacherNames = extractTeacherNames(rootNode);

        createTeacherUsers(teacherNames, teacherRole, department);

    }

    private Set<String> extractTeacherNames(JsonNode rootNode) {
        Set<String> teacherNames = new HashSet<>();

        JsonNode niveauNode = rootNode.get("niveau");
        if (niveauNode == null) {

            return teacherNames;
        }

        niveauNode.fieldNames().forEachRemaining(levelCode -> {
            try {
                JsonNode levelNode = niveauNode.get(levelCode);
                processLevelForTeachers(levelNode, teacherNames);
            } catch (Exception e) {

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

    private void createTeacherUsers(Set<String> teacherNames, Role teacherRole, Department department) {

        for (String fullName : teacherNames) {
            if (createTeacherUser(fullName, teacherRole, department)) {
                continue;
            } else {
                continue;
            }
        }
    }

    private boolean createTeacherUser(String fullName, Role teacherRole, Department department) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        String[] nameParts = parseFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];
        String email = generateEmailFromName(fullName) + DOMAIN_NAME;

        if (userRepository.existsByEmail(email)) {

            return false;
        }

        Teacher teacher = Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(DEFAULT_ADDRESS)
                .phoneNumber(DEFAULT_PHONE)
                .email(email)
                .enabled(true)
                .password(passwordEncoder.encode(firstName.toLowerCase() + passwordSuffix))
                .department(department)
                .role(teacherRole)
                .build();

        teacherRepository.save(teacher);

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

    private boolean isDataAlreadyLoaded() {
        return roomRepository.count() > 0 && ueRepository.count() > 0;
    }

    private Faculty createOrGetFaculty() {
        return facultyRepository.findByCode(FACULTY_CODE)
                .orElseGet(() -> {

                    Faculty faculty = Faculty.builder()
                            .name(FACULTY_NAME)
                            .code(FACULTY_CODE)
                            .rooms(new HashSet<>())
                            .branches(new HashSet<>())
                            .build();
                    return facultyRepository.save(faculty);
                });
    }

    private Department createOrGetDepartment(Branch branch) {
        return departmentRepository.findByCode(DEPARTMENT_CODE)
                .orElseGet(() -> {

                    Department department = departmentRepository.save(Department.builder()
                            .name(DEPARTMENT_NAME)
                            .code(DEPARTMENT_CODE)
                            .branch(branch)
                            .build());
                    branch.setDepartment(department);
                    branchRepository.save(branch);
                    return department;
                });
    }

    private Branch createOrGetBranch(Faculty faculty) {
        return branchRepository.findByCode(BRANCH_CODE)
                .orElseGet(() -> {

                    Branch branch = Branch.builder()
                            .name(BRANCH_NAME)
                            .code(BRANCH_CODE)
                            .faculty(faculty)
                            .levels(new HashSet<>())
                            .build();
                    Branch savedBranch = branchRepository.save(branch);

                    // Update faculty's branches
                    faculty.getBranches().add(savedBranch);
                    facultyRepository.save(faculty);

                    return savedBranch;
                });
    }

    private void loadRoomsFromJson(Faculty faculty) {
        try {
            if (roomRepository.count() > 0) {

                return;
            }

            Resource resource = new ClassPathResource("data/rooms.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            List<Room> createdRooms = processRoomNodes(rootNode);

            if (!createdRooms.isEmpty()) {
                faculty.getRooms().addAll(createdRooms);
                facultyRepository.save(faculty);

            }

        } catch (IOException e) {

            throw new RuntimeException("Failed to load rooms", e);
        }
    }

    private List<Room> processRoomNodes(JsonNode rootNode) {
        List<Room> createdRooms = new ArrayList<>();

        JsonNode informatiqueNode = rootNode.get("Informatique");
        if (informatiqueNode != null && informatiqueNode.isArray()) {
            informatiqueNode.forEach(roomNode -> {
                try {
                    Room room = createRoomFromNode(roomNode);
                    if (room != null) {
                        createdRooms.add(room);
                    }
                } catch (Exception e) {

                }
            });
        }

        return createdRooms;
    }

    private Room createRoomFromNode(JsonNode roomNode) {
        String roomCode = getTextValue(roomNode, "num");
        if (roomCode == null || roomRepository.existsByCode(roomCode)) {
            return null;
        }

        String buildingName = getTextValue(roomNode, "batiment");
        Long capacity = getLongValue(roomNode, "capacite");

        Room room = Room.builder()
                .building(buildingName)
                .code(roomCode)
                .type(determineRoomType(buildingName))
                .capacity(capacity != null ? capacity : 0L)
                .availability(true)
                .build();

        return roomRepository.save(room);
    }

    private void loadSubjectsAndRelatedData(Faculty faculty, Branch branch) {
        try {
            if (ueRepository.count() > 0) {

                return;
            }

            Resource resource = new ClassPathResource("data/subjects.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            JsonNode niveauNode = rootNode.get("niveau");
            if (niveauNode != null) {
                processLevelsAndSubjects(niveauNode, branch);
            }

        } catch (IOException e) {

            throw new RuntimeException("Failed to load subjects", e);
        }
    }

    private void processLevelsAndSubjects(JsonNode niveauNode, Branch branch) {
        niveauNode.fieldNames().forEachRemaining(levelCode -> {
            try {
                Level level = createOrGetLevel(levelCode, branch);
                JsonNode levelNode = niveauNode.get(levelCode);
                processSemesters(levelNode, level);
            } catch (Exception e) {

            }
        });
    }

    private void processSemesters(JsonNode levelNode, Level level) {
        levelNode.fieldNames().forEachRemaining(semester -> {
            try {
                JsonNode semesterNode = levelNode.get(semester);
                JsonNode subjectsArray = semesterNode.get("subjects");
                if (subjectsArray != null && subjectsArray.isArray()) {
                    processSubjects(subjectsArray, level, semester);
                }
            } catch (Exception e) {

            }
        });
    }

    private void processSubjects(JsonNode subjectsArray, Level level, String semester) {
        subjectsArray.forEach(subjectNode -> {
            try {
                Ue ue = createUeFromNode(subjectNode, level, semester);
                if (ue != null) {

                    createCourseFromNode(subjectNode, ue);
                }
            } catch (Exception e) {

            }
        });
    }

    private Level createOrGetLevel(String levelCode, Branch branch) {
        String fullLevelCode = "INFO-L" + levelCode;

        return levelRepository.findByCode(fullLevelCode)
                .orElseGet(() -> {

                    Level level = Level.builder()
                            .code(fullLevelCode)
                            .name("Informatique Niveau " + levelCode)
                            .totalNumberOfStudents(0L)
                            .branch(branch)
                            .build();

                    Level savedLevel = levelRepository.save(level);

                    // Update branch's levels
                    branch.getLevels().add(savedLevel);
                    branchRepository.save(branch);

                    return savedLevel;
                });
    }

    private Ue createUeFromNode(JsonNode subjectNode, Level level, String semester) {
        String subjectCode = getTextValue(subjectNode, "code");
        String subjectName = extractSubjectName(subjectNode);

        if (subjectCode == null || subjectName.isEmpty() || ueRepository.existsByCode(subjectCode)) {
            return null;
        }

        Long credits = getLongValue(subjectNode, "credit");
        String category = getTextValue(subjectNode, "category");

        Ue ue = Ue.builder()
                .name(subjectName)
                .code(subjectCode)
                .credits(Objects.requireNonNullElse(credits, 6).intValue())
                .hourlyCharge(Objects.requireNonNullElse(credits, 6L).intValue() * 10)
                .category(category)
                .level(level)
                .build();

        return ueRepository.save(ue);
    }

    private String extractSubjectName(JsonNode subjectNode) {
        JsonNode nameNode = subjectNode.get("name");

        if (nameNode == null) {
            return "Unknown Subject";
        }

        if (nameNode.isTextual()) {
            String name = nameNode.asText().trim();
            return name.isEmpty() ? "Unknown Subject" : name;
        }

        if (nameNode.isArray()) {
            for (JsonNode nameElement : nameNode) {
                String name = nameElement.asText().trim();
                if (!name.isEmpty()) {
                    return name;
                }
            }
        }

        return "Unknown Subject";
    }

    private RoomType determineRoomType(String batiment) {
        if (batiment == null) {
            return RoomType.CLASSROOM;
        }

        return switch (batiment.toUpperCase()) {
            case "AMPHI" -> RoomType.AMPHITHEATER;
            case "EXTENSION 1", "EXTENSION 2", "BLOC PEDAGOGIQUE" -> RoomType.CLASSROOM;
            default -> RoomType.CLASSROOM;
        };
    }

    // Utility methods for safe JSON node value extraction
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private Long getLongValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull()) {
            if (fieldNode.isNumber()) {
                return fieldNode.asLong();
            }
            try {
                return Long.parseLong(fieldNode.asText());
            } catch (NumberFormatException e) {

            }
        }
        return null;
    }
}