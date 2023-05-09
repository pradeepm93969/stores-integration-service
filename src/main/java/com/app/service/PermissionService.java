package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.exception.CustomApplicationException;
import com.app.repository.PermissionRepository;
import com.app.repository.entity.Permission;

@Service
public class PermissionService {
	
	@Autowired
	private PermissionRepository repository;
	
	@Cacheable(value = "Permission")
	public List<Permission> findAll() {
		return repository.findAll();
	}
	
	@Cacheable(value = "Permission", key = "#id")
	public Permission getById(String id) {
		return repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
}
