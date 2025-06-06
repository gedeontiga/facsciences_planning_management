package com.facsciences_planning_management.facsciences_planning_management.entities.types;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.types.PermissionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoleType {
	DEPARTMENT_CHIEF(
			Set.of(
					PermissionType.DEPARTMENT_CHIEF_READ_PLANNING,
					PermissionType.DEPARTMENT_CHIEF_CUSTOMIZE_PLANNING)),
	ADMIN(
			Set.of(
					PermissionType.ADMIN_CREATE_ALL,
					PermissionType.ADMIN_READ_ALL,
					PermissionType.ADMIN_UPDATE_ALL,
					PermissionType.ADMIN_DELETE_ALL)),
	SECRETARY(
			Set.of(
					PermissionType.SECRETARY_READ_PLANNING,
					PermissionType.SECRETARY_UPDATE_PLANNING,
					PermissionType.SECRETARY_DELETE_PLANNING)),
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
