package com.facsciencesuy1.planning_management.api_gateway.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciencesuy1.planning_management.api_gateway.repositories.UserRepository;
import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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
}
