package com.app.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.repository.entity.IntegrationLog;
import com.app.service.IntegrationLogService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/integrationLog")
@Tag(name = "2. Admin Management")
@SecurityRequirement(name = "bearerAuth")
public class IntegrationLogController {
	
	@Autowired
	private IntegrationLogService service;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_INTEGRATIONS','ROLE_MANAGE_INTEGRATIONS','ROLE_ADMIN')")
	public Page<IntegrationLog> get(
		@And({
	        @Spec(path = "identifier", params = "identifier", spec = LikeIgnoreCase.class),
	        @Spec(path = "subIdentifier", params = "subIdentifier", spec = LikeIgnoreCase.class),
	        @Spec(path = "operationType", params = "operationType", spec = LikeIgnoreCase.class),
	        @Spec(path = "systemName", params = "systemName", spec = LikeIgnoreCase.class),
	        @Spec(path = "createdAt", params = {"createdAtGt","createdAtLt"}, spec = Between.class)
		}) Specification<IntegrationLog> spec,
		
		@Positive @RequestParam(defaultValue = "1") Integer pageNo, 
		@Positive @RequestParam(defaultValue = "10") Integer pageSize,
	    @RequestParam(defaultValue = "createdAt") String sortBy,
	    @RequestParam(defaultValue = "desc") String sortDirection) {
		return service.get(spec, pageNo-1, pageSize, sortBy, sortDirection);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_INTEGRATIONS','ROLE_MANAGE_INTEGRATIONS','ROLE_ADMIN')")
	public IntegrationLog getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") Long id) {
		return service.getById(id);
	}
	
	@GetMapping("/{id}/request")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_INTEGRATIONS','ROLE_MANAGE_INTEGRATIONS','ROLE_ADMIN')")
	public ResponseEntity<byte[]> getRequestById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") Long id) {
		IntegrationLog log = service.getById(id);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    return new ResponseEntity<>(log.getRequest(), headers, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/response")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_INTEGRATIONS','ROLE_MANAGE_INTEGRATIONS','ROLE_ADMIN')")
	public ResponseEntity<byte[]> getResponseById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") Long id) {
		IntegrationLog log = service.getById(id);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    return new ResponseEntity<>(log.getResponse(), headers, HttpStatus.OK);
	}
	
}
