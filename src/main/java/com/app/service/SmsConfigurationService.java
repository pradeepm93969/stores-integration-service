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
import com.app.repository.SmsConfigurationRepository;
import com.app.repository.entity.SmsConfiguration;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class SmsConfigurationService {
	
	@Autowired
	private SmsConfigurationRepository repository;
	
	public Page<SmsConfiguration> get(Specification<SmsConfiguration> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<SmsConfiguration> get(Specification<SmsConfiguration> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}

	@Cacheable(value = "SmsConfiguration", key = "#id")
	public SmsConfiguration getById(String id) {
		return repository.findById(id).orElseThrow(() -> new CustomApplicationException(
				HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public SmsConfiguration create(SmsConfiguration request) {
		Optional<SmsConfiguration> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		return repository.save(request);
	}

	@CachePut(value = "SmsConfiguration", key = "#id")
	public SmsConfiguration update(String id, SmsConfiguration request) {
		getById(id);
		request.setId(id);
		return repository.save(request);
	}

	@CacheEvict(value = "SmsConfiguration", key = "#id")
	public void delete(String id) {
		getById(id);
		repository.deleteById(id);
	}

}
