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
import com.app.repository.ClientRepository;
import com.app.repository.entity.Client;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;
	
	public Page<Client> get(Specification<Client> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<Client> get(Specification<Client> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}

	@Cacheable(value = "Client", key = "#id")
	public Client getById(String id) {
		return repository.findById(id)
				.orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public Client create(Client request) {
		Optional<Client> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		request.setScope("DEFAULT");
		request.setDefault(false);
		
		return repository.save(request);
	}

	@CachePut(value = "Client", key = "#id")
	public Client update(String id, Client request) {
		Client client = getById(id);
		client.setClientSecret(request.getClientSecret());
		client.setAuthorizedGrantTypes(request.getAuthorizedGrantTypes());
		client.setAuthorities(request.getAuthorities());
		client.setAccessTokenValiditySeconds(request.getAccessTokenValiditySeconds());
		client.setRefreshTokenValiditySeconds(request.getRefreshTokenValiditySeconds());
		return repository.save(client);
	}

	@CacheEvict(value = "Client", key = "#id")
	public void delete(String id) {
		Client client = getById(id);
		if (client.isDefault()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "DEFAULT_CLIENT_DELETE", "default client cannot be deleted");
		}
		repository.deleteById(id);
	}
	
}
