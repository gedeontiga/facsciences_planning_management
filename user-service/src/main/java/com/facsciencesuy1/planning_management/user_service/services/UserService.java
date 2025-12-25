package com.facsciencesuy1.planning_management.user_service.services;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;
import com.facsciencesuy1.planning_management.user_service.repositories.UserRepository;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.UserDTO;
import com.facsciencesuy1.planning_management.user_service.utils.dtos.UserUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDTO getUser() {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new CustomBusinessException("User not found"));
        return new UserDTO(user);
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
