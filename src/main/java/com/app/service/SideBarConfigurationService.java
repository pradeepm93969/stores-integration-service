package com.app.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.exception.CustomApplicationException;
import com.app.repository.SideBarConfigurationRepository;
import com.app.repository.entity.SideBarConfiguration;

@Service
public class SideBarConfigurationService {
	
	@Autowired
	private SideBarConfigurationRepository repository;
	
	public Page<SideBarConfiguration> get(Specification<SideBarConfiguration> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<SideBarConfiguration> get(Specification<SideBarConfiguration> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}
	
	@Cacheable(value = "SideBarConfiguration")
	public List<SideBarConfiguration> getAll() {
		return repository.findAllByOrderBySequenceAsc();
	}
	
	@Cacheable(value = "SideBarConfiguration", key = "#id")
	public SideBarConfiguration getById(String id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public SideBarConfiguration create(SideBarConfiguration request) {
		Optional<SideBarConfiguration> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		return repository.save(request);
	}

	@Caching(put = @CachePut(value = "SideBarConfiguration", key = "#id"),
	            evict = @CacheEvict(cacheNames = "SideBarConfiguration"))
	public SideBarConfiguration update(String id, SideBarConfiguration request) {
		getById(id);
		request.setId(id);
		return repository.save(request);
	}

	@Caching(evict = { @CacheEvict(value = "SideBarConfiguration", key = "#id"),
			@CacheEvict(value = "SideBarConfiguration") })	
	public void delete(String id) {
		getById(id);
		repository.deleteById(id);
	}

}
