package com.app.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.repository.entity.SmsConfiguration;
import com.app.service.CsvService;
import com.app.service.SmsConfigurationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/smsConfigurations")
@Tag(name = "2. Admin Management")
@SecurityRequirement(name = "bearerAuth")
public class SmsConfigurationsController {
	
	@Autowired
	private SmsConfigurationService service;
	
	@Autowired
	private CsvService csvService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_CONFIGURATIONS','ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public Page<SmsConfiguration> get(
			@And({
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "message", params = "message", spec = LikeIgnoreCase.class),
                @Spec(path = "whitelisted", params = "whitelisted", spec = Equal.class),
                @Spec(path = "whitelistedNumbers", params = "whitelistedNumbers", spec = LikeIgnoreCase.class)
			}) Specification<SmsConfiguration> spec,
			
			@Positive @RequestParam(defaultValue = "1") Integer pageNo, 
			@Positive @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
		return service.get(spec, pageNo-1, pageSize, sortBy, sortDirection);
	}
	
	@GetMapping("/extract")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_CONFIGURATIONS','ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public ResponseEntity<Resource> extract(
			@And({
				@Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "subject", params = "subject", spec = LikeIgnoreCase.class),
                @Spec(path = "allowedDomain", params = "allowedDomain", spec = LikeIgnoreCase.class)
			}) Specification<SmsConfiguration> spec,
			
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) throws IOException {
		List<SmsConfiguration> smsConfigurations = service.get(spec, sortBy, sortDirection);
		
		List<String> noNeededColumn = Arrays.asList(new String[] {"createdAt", "createdBy", "updatedAt", "updatedBy" });
		Resource resource = csvService.generateCsvFile(smsConfigurations, noNeededColumn);

        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SmsConfigurations_" + now + ".csv\"")
                             .body(resource);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_CONFIGURATIONS','ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SmsConfiguration getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") String id) {
		return service.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SmsConfiguration create(@Valid @RequestBody SmsConfiguration request) {
		return service.create(request);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SmsConfiguration update(@PathVariable("id") @NotBlank(message = "id is mandatory") String id,
			@Valid @RequestBody SmsConfiguration request) {
		return service.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public void delete(@PathVariable("id") @NotBlank(message = "id is mandatory") String id) {
		service.delete(id);
	}
}
