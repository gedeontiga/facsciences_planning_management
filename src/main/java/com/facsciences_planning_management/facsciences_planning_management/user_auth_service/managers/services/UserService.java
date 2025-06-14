package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.EntityNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserUpdate;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.AccountNotActivatedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserResponse getUser() {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserResponse(user);
    }

    @Override
    @Transactional
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
            throw new EntityNotFoundException("User not found");
        }
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found."));
    }

    public UserResponse updateUser(UserUpdate user) {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Users updatedUser = getUserByEmail(email);
        updatedUser.setFirstName(Optional.ofNullable(user.firstName()).orElse(updatedUser.getFirstName()));
        updatedUser.setLastName(Optional.ofNullable(user.lastName()).orElse(updatedUser.getLastName()));
        updatedUser.setAddress(Optional.ofNullable(user.address()).orElse(updatedUser.getAddress()));
        updatedUser.setPhoneNumber(Optional.ofNullable(user.phoneNumber()).orElse(updatedUser.getPhoneNumber()));
        return new UserResponse(userRepository.save(updatedUser));
    }

    public void deleteUser(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new EntityNotFoundException("Cannot delete: User with email " + email + " not found");
        }
        userRepository.deleteByEmail(email);
    }
}
