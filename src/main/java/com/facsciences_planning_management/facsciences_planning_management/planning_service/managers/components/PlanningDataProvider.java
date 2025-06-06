package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.components;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Faculty;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.BranchRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.DepartmentRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.FacultyRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanningDataProvider {

    private final ObjectMapper objectMapper;
    private final RoomRepository roomRepository;
    private final UeRepository ueRepository;
    private final LevelRepository levelRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    private static final String FACULTY_CODE = "FACSCIENCES";
    private static final String FACULTY_NAME = "Faculté Des Sciences";
    private static final String DEPARTMENT_CODE = "DEPT-INFO";
    private static final String DEPARTMENT_NAME = "Département Informatique";
    private static final String BRANCH_CODE = "INFO";
    private static final String BRANCH_NAME = "Informatique";

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadInitialData() {
        try {
            log.info("Starting initial data loading...");

            if (isDataAlreadyLoaded()) {
                log.info("Data already loaded, skipping initialization");
                return;
            }

            Faculty faculty = createOrGetFaculty();
            Department department = createOrGetDepartment();
            Branch branch = createOrGetBranch(faculty, department);

            loadRoomsFromJson(faculty);
            loadSubjectsAndRelatedData(faculty, branch);

            log.info("Initial data loading completed successfully");

        } catch (Exception e) {
            log.error("Failed to load initial data", e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private boolean isDataAlreadyLoaded() {
        return roomRepository.count() > 0 && ueRepository.count() > 0;
    }

    private Faculty createOrGetFaculty() {
        return facultyRepository.findByCode(FACULTY_CODE)
                .orElseGet(() -> {
                    log.info("Creating new faculty: {}", FACULTY_NAME);
                    Faculty faculty = Faculty.builder()
                            .name(FACULTY_NAME)
                            .code(FACULTY_CODE)
                            .rooms(new HashSet<>())
                            .branches(new HashSet<>())
                            .build();
                    return facultyRepository.save(faculty);
                });
    }

    private Department createOrGetDepartment() {
        return departmentRepository.findByCode(DEPARTMENT_CODE)
                .orElseGet(() -> {
                    log.info("Creating new department: {}", DEPARTMENT_NAME);
                    Department department = Department.builder()
                            .name(DEPARTMENT_NAME)
                            .code(DEPARTMENT_CODE)
                            .build();
                    return departmentRepository.save(department);
                });
    }

    private Branch createOrGetBranch(Faculty faculty, Department department) {
        return branchRepository.findByCode(BRANCH_CODE)
                .orElseGet(() -> {
                    log.info("Creating new branch: {}", BRANCH_NAME);
                    Branch branch = Branch.builder()
                            .name(BRANCH_NAME)
                            .code(BRANCH_CODE)
                            .department(department)
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
                log.info("Rooms already loaded, skipping room initialization");
                return;
            }

            log.info("Loading rooms from JSON...");
            Resource resource = new ClassPathResource("data/rooms.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            List<Room> createdRooms = processRoomNodes(rootNode);

            if (!createdRooms.isEmpty()) {
                faculty.getRooms().addAll(createdRooms);
                facultyRepository.save(faculty);
                log.info("Created {} rooms", createdRooms.size());
            }

        } catch (IOException e) {
            log.error("Error loading rooms from JSON", e);
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
                    log.warn("Failed to create room from node: {}", roomNode, e);
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
                log.info("Subjects already loaded, skipping subject initialization");
                return;
            }

            log.info("Loading subjects from JSON...");
            Resource resource = new ClassPathResource("data/subjects.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            JsonNode niveauNode = rootNode.get("niveau");
            if (niveauNode != null) {
                processLevelsAndSubjects(niveauNode, branch);
            }

        } catch (IOException e) {
            log.error("Error loading subjects from JSON", e);
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
                log.warn("Failed to process level: {}", levelCode, e);
            }
        });
    }

    private Level createOrGetLevel(String levelCode, Branch branch) {
        String fullLevelCode = "INFO-L" + levelCode;

        return levelRepository.findByCode(fullLevelCode)
                .orElseGet(() -> {
                    log.info("Creating new level: {}", fullLevelCode);
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

    private void processSemesters(JsonNode levelNode, Level level) {
        levelNode.fieldNames().forEachRemaining(semester -> {
            try {
                JsonNode semesterNode = levelNode.get(semester);
                JsonNode subjectsArray = semesterNode.get("subjects");

                if (subjectsArray != null && subjectsArray.isArray()) {
                    processSubjects(subjectsArray, level, semester);
                }

            } catch (Exception e) {
                log.warn("Failed to process semester: {} for level: {}", semester, level.getCode(), e);
            }
        });
    }

    private void processSubjects(JsonNode subjectsArray, Level level, String semester) {
        subjectsArray.forEach(subjectNode -> {
            try {
                Ue ue = createUeFromNode(subjectNode, level, semester);
                if (ue != null) {
                    log.debug("Created UE: {} for level: {}", ue.getCode(), level.getCode());
                }
            } catch (Exception e) {
                log.warn("Failed to create UE from node: {}", subjectNode, e);
            }
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
                .credits(credits != null ? credits : 0L)
                .duration(Duration.ofHours(3))
                .hourlyCharge(credits != null ? credits.intValue() * 10 : 0)
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
                log.warn("Invalid number format for field {}: {}", fieldName, fieldNode.asText());
            }
        }
        return null;
    }
}