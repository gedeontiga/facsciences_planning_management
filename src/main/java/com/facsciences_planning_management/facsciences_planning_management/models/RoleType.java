package com.facsciences_planning_management.facsciences_planning_management.models;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoleType {
        DEPARTMENT_CHIEF(
                        Set.of(
                                        PermissionType.DEPARTMENT_CHIEF_READ_PLANNING,
                                        PermissionType.DEPARTMENT_CHIEF_CUSTOMIZE_PLANNING)),
        DEAN(
                        Set.of(
                                        PermissionType.DEAN_CREATE_ALL,
                                        PermissionType.DEAN_READ_ALL,
                                        PermissionType.DEAN_UPDATE_ALL,
                                        PermissionType.DEAN_DELETE_ALL)),
        VICE_DEAN(
                        Set.of(
                                        PermissionType.VICE_DEAN_CREATE_PLANNING,
                                        PermissionType.VICE_DEAN_READ_PLANNING,
                                        PermissionType.VICE_DEAN_UPDATE_PLANNING,
                                        PermissionType.VICE_DEAN_DELETE_PLANNING)),
        TEACHER(
                        Set.of(
                                        PermissionType.TEACHER_READ_PLANNING)),
        STUDENT(
                        Set.of(
                                        PermissionType.STUDENT_GET_PLANNING));

        @Getter
        private Set<PermissionType> permissions;

        public Collection<? extends GrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> grantedAuthorities = permissions.stream().map(
                                permission -> new SimpleGrantedAuthority(permission.name()))
                                .collect(Collectors.toList());

                grantedAuthorities.add(new SimpleGrantedAuthority(this.name()));
                return grantedAuthorities;
        }
}
