package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.UserNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.models.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserResponse getUser(String email) {
        Users user = (Users) userRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponse(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new RuntimeException("User is not activated"));
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }

    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }
}
