package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.exception.CustomApplicationException;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.repository.entity.Permission;
import com.app.repository.entity.Role;
import com.app.repository.entity.User;

@Service
public class RoleService {
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private RoleRepository repository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Page<Role> get(Specification<Role> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<Role> get(Specification<Role> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}

	@Cacheable(value = "Role", key = "#id")
	public Role getById(String id) {
		Role role = repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
		
		List<Permission> permissions = permissionService.findAll();
		Map<String, List<Permission>> permissionsMap = new HashMap<>();
		
		for (Permission entry : permissions) {
			permissionsMap.computeIfAbsent(entry.getGroup(), k -> new ArrayList<>());
			Permission permissionDto = new Permission();
			permissionDto.setId(entry.getId());
			permissionDto.setName(entry.getName());
			permissionDto.setGroup(entry.getGroup());
			permissionDto.setEnabled(role.getPermissions().stream().anyMatch(p -> p.getId().equals(entry.getId())));
			permissionsMap.get(entry.getGroup()).add(permissionDto);
		}
		
		role.setPermissionsMap(permissionsMap);
		return role;
	}

	public Role create(Role request) {
		Optional<Role> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		var permissions = new HashSet<Permission>();
		request.getPermissionsMap().forEach((k, v) -> {
			v.forEach(p -> {
				if (p.isEnabled()) {
					permissions.add(permissionService.getById(p.getId()));
				}
			});
		});
		request.setPermissions(permissions);
		Role entity = repository.save(request);
		return getById(entity.getId());
	}

	@CachePut(value = "Role", key = "#id")
	public Role update(String id, Role request) {
		if (id.toUpperCase().equals("ROLE_ADMIN"))
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "UNAUTHORIZED", "Unauthorized");
		
		repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
		
		request.setId(id);
		var permissions = new HashSet<Permission>();
		request.getPermissionsMap().forEach((k, v) -> {
			v.forEach(p -> {
				if (p.isEnabled()) {
					permissions.add(permissionService.getById(p.getId()));
				}
			});
		});
		request.setPermissions(permissions);
		Role entity = repository.save(request);
		return getById(entity.getId());
	}

	@CacheEvict(value = "Role", key = "#id")
	public void delete(String id) {
		if ("ROLE_ADMIN".equals(id)) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", 
					"Bad Request");
		}
		Role role = repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
		
		List<User> usersList = userRepository.findAll();
		for (User user : usersList) {
			if (user.getRoles().contains(role)) {
				throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ROLE_LINKED_WITH_USERS", 
						"Role is already linked with a User");
			}
		}
		repository.deleteById(id);
	}
}
