package com.facsciences_planning_management.facsciences_planning_management.entities;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Document
public class Users implements UserDetails {
    @Id
    private String id;

    @NonNull
    @Indexed(unique = true)
    private String email;
    @NonNull
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;

    @DocumentReference(lazy = true, collection = "roles")
    @NonNull
    private Role role;

    @NonNull
    private Boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getType().getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
