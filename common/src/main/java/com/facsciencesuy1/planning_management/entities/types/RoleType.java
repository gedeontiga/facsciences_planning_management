package com.facsciencesuy1.planning_management.entities.types;

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
	DEPARTMENT_HEAD(
			Set.of(
					PermissionType.READ_PLANNING)),
	ADMIN(
			Set.of(
					PermissionType.CREATE_PLANNING,
					PermissionType.READ_PLANNING,
					PermissionType.UPDATE_PLANNING,
					PermissionType.DELETE_PLANNING)),
	SECRETARY(
			Set.of(
					PermissionType.READ_PLANNING,
					PermissionType.UPDATE_PLANNING)),
	TEACHER(
			Set.of(
					PermissionType.READ_PLANNING)),
	PROCTOR(
			Set.of(
					PermissionType.READ_PLANNING)),
	STUDENT(
			Set.of(
					PermissionType.READ_PLANNING));

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
