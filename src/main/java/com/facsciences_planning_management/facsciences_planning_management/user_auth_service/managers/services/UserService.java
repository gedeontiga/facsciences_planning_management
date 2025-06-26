package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDTO getUser() {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomBusinessException("User not found"));
        return new UserDTO(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        try {
            return userRepository.findByEmailAndEnabledIsTrue(email)
                    .orElseThrow(() -> new CustomBusinessException("User is not activated"));
        } catch (Exception e) {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomBusinessException("User not found"));
            if (!user.isEnabled()) {
                throw new CustomBusinessException("User is not activated");
            }
            throw new CustomBusinessException("User not found");
        }
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomBusinessException("User with email " + email + " not found."));
    }

    public UserDTO updateUser(UserUpdate user) {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Users updatedUser = getUserByEmail(email);
        updatedUser.setFirstName(Optional.ofNullable(user.firstName()).orElse(updatedUser.getFirstName()));
        updatedUser.setLastName(Optional.ofNullable(user.lastName()).orElse(updatedUser.getLastName()));
        updatedUser.setAddress(Optional.ofNullable(user.address()).orElse(updatedUser.getAddress()));
        updatedUser.setPhoneNumber(Optional.ofNullable(user.phoneNumber()).orElse(updatedUser.getPhoneNumber()));
        return new UserDTO(userRepository.save(updatedUser));
    }

    public void deleteUser(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new CustomBusinessException("Cannot delete: User with email " + email + " not found");
        }
        userRepository.deleteByEmail(email);
    }
}
