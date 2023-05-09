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

import com.app.repository.entity.SchedulerConfiguration;
import com.app.service.CsvService;
import com.app.service.SchedulerConfigurationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/schedulerConfigurations")
@Tag(name = "2. Admin Management")
@SecurityRequirement(name = "bearerAuth")
public class SchedulerConfigurationController {
	
	@Autowired
	
	private SchedulerConfigurationService service;
	
	@Autowired
	private CsvService csvService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_CONFIGURATIONS','ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public Page<SchedulerConfiguration> get(
			@And({
				@Spec(path = "id", params = "id", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "status", params = "status", spec = LikeIgnoreCase.class),
                @Spec(path = "threadCount", params = "threadCount", spec = Equal.class),
                @Spec(path = "retryCount", params = "retryCount", spec = Equal.class),
                @Spec(path = "batchSize", params = "batchSize", spec = Equal.class),
                @Spec(path = "restartSeconds", params = "restartSeconds", spec = Equal.class),
                @Spec(path = "exponentialFactor", params = "exponentialFactor", spec = Equal.class),
                @Spec(path = "nextRunSeconds", params = "nextRunSeconds", spec = Equal.class),
                @Spec(path = "debug", params = "debug", spec = Equal.class)
			}) Specification<SchedulerConfiguration> spec,
			
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
				@Spec(path = "id", params = "id", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "status", params = "status", spec = LikeIgnoreCase.class),
                @Spec(path = "threadCount", params = "threadCount", spec = Equal.class),
                @Spec(path = "retryCount", params = "retryCount", spec = Equal.class),
                @Spec(path = "batchSize", params = "batchSize", spec = Equal.class),
                @Spec(path = "restartSeconds", params = "restartSeconds", spec = Equal.class),
                @Spec(path = "exponentialFactor", params = "exponentialFactor", spec = Equal.class),
                @Spec(path = "nextRunSeconds", params = "nextRunSeconds", spec = Equal.class),
                @Spec(path = "debug", params = "debug", spec = LikeIgnoreCase.class)
			}) Specification<SchedulerConfiguration> spec,
			
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) throws IOException {
		List<SchedulerConfiguration> configurations = service.get(spec, sortBy, sortDirection);
		
		List<String> noNeededColumn = Arrays.asList(new String[] {"createdAt", "createdBy", "updatedAt", "updatedBy" });
		Resource resource = csvService.generateCsvFile(configurations, noNeededColumn);

        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SchedulerConfigurations_" + now + ".csv\"")
                             .body(resource);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_CONFIGURATIONS','ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SchedulerConfiguration getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") String id) {
		return service.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SchedulerConfiguration create(@Valid @RequestBody SchedulerConfiguration request) {
		return service.create(request);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public SchedulerConfiguration update(@PathVariable("id") @NotBlank(message = "id is mandatory") String id,
			@Valid @RequestBody SchedulerConfiguration request) {
		return service.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_CONFIGURATIONS','ROLE_ADMIN')")
	public void delete(@PathVariable("id") @NotBlank(message = "id is mandatory") String id) {
		service.delete(id);
	}
}
