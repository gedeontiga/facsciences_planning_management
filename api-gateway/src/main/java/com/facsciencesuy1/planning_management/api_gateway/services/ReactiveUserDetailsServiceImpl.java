package com.facsciencesuy1.planning_management.api_gateway.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Reactive version of UserDetailsService for WebFlux
 */
@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService; // Your existing UserService

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> userService.loadUserByUsername(username))
                .onErrorMap(e -> new UsernameNotFoundException("User not found: " + username));
    }
}
