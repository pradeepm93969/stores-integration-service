package com.app.service;

import java.util.List;
import java.util.Optional;

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
import com.app.repository.EmailConfigurationRepository;
import com.app.repository.entity.EmailConfiguration;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class EmailConfigurationService {
	
	@Autowired
	private EmailConfigurationRepository repository;
	
	public Page<EmailConfiguration> get(Specification<EmailConfiguration> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<EmailConfiguration> get(Specification<EmailConfiguration> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}

	@Cacheable(value = "EmailConfiguration", key = "#id")
	public EmailConfiguration getById(String id) {
		return repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public EmailConfiguration create(EmailConfiguration request) {
		Optional<EmailConfiguration> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		return repository.save(request);
	}

	@CachePut(value = "EmailConfiguration", key = "#id")
	public EmailConfiguration update(String id, EmailConfiguration request) {
		getById(id);
		request.setId(id);
		return repository.save(request);
	}

	@CacheEvict(value = "EmailConfiguration", key = "#id")
	public void delete(String id) {
		getById(id);
		repository.deleteById(id);
	}

}
