package com.facsciencesuy1.planning_management.user_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciencesuy1.planning_management.dtos.RoleDTO;
import com.facsciencesuy1.planning_management.entities.Role;
import com.facsciencesuy1.planning_management.entities.Student;
import com.facsciencesuy1.planning_management.entities.Teacher;
import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.entities.types.RoleType;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;
import com.facsciencesuy1.planning_management.user_service.repositories.DepartmentRepository;
import com.facsciencesuy1.planning_management.user_service.repositories.LevelRepository;
import com.facsciencesuy1.planning_management.user_service.repositories.RoleRepository;
import com.facsciencesuy1.planning_management.user_service.repositories.StudentRepository;
import com.facsciencesuy1.planning_management.user_service.repositories.TeacherRepository;
import com.facsciencesuy1.planning_management.user_service.repositories.UserRepository;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.UserDTO;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.admin.CreateStudent;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.admin.CreateTeacher;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.admin.CreateUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServices {

    @Value("${app.password-suffix}")
    private String passwordEnd;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService mailNotificationService;
    private final LevelRepository levelRepository;
    private final DepartmentRepository departmentRepository;

    public UserDTO createUser(CreateUser user) {
        if (!user.role().equals(RoleType.ADMIN) && !user.role().equals(RoleType.SECRETARY)) {
            throw new CustomBusinessException("This role is not allowed as simple user or admin");
        }
        if (!user.role().equals(RoleType.PROCTOR)) {
            throw new CustomBusinessException("This role is not allowed as proctor");
        }
        if (userRepository.existsByEmail(user.email())) {
            throw new CustomBusinessException("User already exists");
        }
        Role role = roleRepository.findByType(user.role())
                .orElseThrow(() -> new CustomBusinessException("Role not found"));
        final String DEFAULT_PASSWORD = user.firstName().toLowerCase() + passwordEnd;
        Users savedUser = userRepository.save(Users.builder().firstName(user.firstName()).lastName(user.lastName())
                .email(user.email()).address(user.address()).phoneNumber(user.phoneNumber())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD)).role(role).enabled(true).build());

        sendEmail(savedUser.getEmail(), savedUser.getFirstName(), DEFAULT_PASSWORD, user.role().name());

        return new UserDTO(savedUser);
    }

    public UserDTO createTeacher(CreateTeacher teacher) {
        if (!teacher.role().equals(RoleType.TEACHER) && !teacher.role().equals(RoleType.DEPARTMENT_HEAD)) {
            throw new CustomBusinessException("This role is not allowed as teacher");
        }
        Role role = roleRepository.findByType(RoleType.TEACHER)
                .orElseThrow(() -> new CustomBusinessException("Role not found"));
        if (!departmentRepository.existsById(teacher.departmentId())) {
            throw new CustomBusinessException("Department not found");
        }
        if (userRepository.existsByEmail(teacher.email())) {
            throw new CustomBusinessException("Teacher already exists");
        }
        final String DEFAULT_PASSWORD = teacher.firstName().toLowerCase() + passwordEnd;
        Teacher user = Teacher.builder().firstName(teacher.firstName()).lastName(teacher.lastName())
                .email(teacher.email()).address(teacher.address()).phoneNumber(teacher.phoneNumber())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD)).role(role).enabled(true)
                .departmentId(teacher.departmentId()).build();
        Teacher savedUser = teacherRepository.save(user);
        sendEmail(savedUser.getEmail(), savedUser.getFirstName(), DEFAULT_PASSWORD, teacher.role().name());
        return new UserDTO(savedUser);
    }

    public UserDTO createStudent(CreateStudent student) {
        if (!student.role().equals(RoleType.STUDENT)) {
            throw new CustomBusinessException("This role is not allowed as student");
        }
        if (!levelRepository.existsById(student.levelId())) {
            throw new CustomBusinessException("Level not found");
        }
        if (userRepository.existsByEmail(student.email())) {
            throw new CustomBusinessException("User already exists");
        }
        Role role = roleRepository.findByType(RoleType.TEACHER)
                .orElseThrow(() -> new CustomBusinessException("Role not found"));
        final String DEFAULT_PASSWORD = student.firstName().toLowerCase() + passwordEnd;
        Student user = Student.builder().firstName(student.firstName()).lastName(student.lastName())
                .email(student.email()).address(student.address()).phoneNumber(student.phoneNumber())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD)).role(role).enabled(true).levelId(student.levelId())
                .build();
        Student savedUser = studentRepository.save(user);
        sendEmail(savedUser.getEmail(), savedUser.getFirstName(), DEFAULT_PASSWORD, student.role().name());
        return new UserDTO(savedUser);
    }

    public Page<UserDTO> getUserByRole(String roleId, Pageable page) {
        Page<Users> userPage = userRepository.findByRoleId(roleId, page);
        return userPage.map(UserDTO::new);
    }

    public Page<UserDTO> getUserByRole(RoleType role, Pageable page) {
        Page<Users> userPage = userRepository.findByRoleType(role, page);
        return userPage.map(UserDTO::new);
    }

    private void sendEmail(String email, String firstName, String password, String role) {
        try {
            mailNotificationService.sendAccountCreationEmail(email, firstName, password, role);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send account creation email:" + e.getMessage(), e.getCause());
        }
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(role -> role.toDTO()).collect(Collectors.toList());
    }

    public void disableUser(String id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("User not found with id: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
