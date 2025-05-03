package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.AccountNotActivatedException;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.UserNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.models.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserResponse getUser(String email) {
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponse(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        try {
            return userRepository.findByEmailAndEnabledIsTrue(email)
                    .orElseThrow(() -> new AccountNotActivatedException("User is not activated"));
        } catch (Exception e) {
            if (e instanceof AccountNotActivatedException) {
                throw e;
            }
            // Check if user exists but is not activated
            if (userRepository.findByEmail(email).isPresent()) {
                throw new AccountNotActivatedException("User is not activated");
            }
            throw new UserNotFoundException("User not found");
        }
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }

    public void deleteUser(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new UserNotFoundException("Cannot delete: User with email " + email + " not found");
        }
        userRepository.deleteByEmail(email);
    }
}
