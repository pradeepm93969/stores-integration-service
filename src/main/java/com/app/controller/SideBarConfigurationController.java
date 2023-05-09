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

import com.app.repository.entity.SideBarConfiguration;
import com.app.service.CsvService;
import com.app.service.SideBarConfigurationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/sidebar")
@Tag(name = "2. Admin Management")
@SecurityRequirement(name = "bearerAuth")
public class SideBarConfigurationController {
	
	@Autowired
	private SideBarConfigurationService service;
	
	@Autowired
	private CsvService csvService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_SIDEBARS','ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public Page<SideBarConfiguration> get(
			@And({
                @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                @Spec(path = "icon", params = "icon", spec = LikeIgnoreCase.class),
                @Spec(path = "parentId", params = "parentId", spec = LikeIgnoreCase.class),
                @Spec(path = "link", params = "link", spec = LikeIgnoreCase.class),
                @Spec(path = "sequence", params = "sequence", spec = Equal.class),
                @Spec(path = "authorities", params = "authorities", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "header", params = "header", spec = Equal.class),
                @Spec(path = "root", params = "root", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<SideBarConfiguration> spec,
			
			@Positive @RequestParam(defaultValue = "1") Integer pageNo, 
			@Positive @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "sequence") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
		return service.get(spec, pageNo-1, pageSize, sortBy, sortDirection);
	}
	
	@GetMapping("/extract")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_SIDEBARS','ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public ResponseEntity<Resource> extract(
			@And({
				@Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                @Spec(path = "icon", params = "icon", spec = LikeIgnoreCase.class),
                @Spec(path = "parentId", params = "parentId", spec = LikeIgnoreCase.class),
                @Spec(path = "link", params = "link", spec = LikeIgnoreCase.class),
                @Spec(path = "sequence", params = "sequence", spec = Equal.class),
                @Spec(path = "authorities", params = "authorities", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "header", params = "header", spec = Equal.class),
                @Spec(path = "root", params = "root", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<SideBarConfiguration> spec,
			
            @RequestParam(defaultValue = "sequence") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) throws IOException {
		List<SideBarConfiguration> clients = service.get(spec, sortBy, sortDirection);
		
		List<String> noNeededColumn = Arrays.asList(new String[] { "createdAt", "createdBy", "updatedAt", "updatedBy" });
		Resource resource = csvService.generateCsvFile(clients, noNeededColumn);

        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SideBarConfiguration_" + now + ".csv\"")
                             .body(resource);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_SIDEBARS','ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public SideBarConfiguration getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") String id) {
		return service.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public SideBarConfiguration create(@Valid @RequestBody SideBarConfiguration request) {
		return service.create(request);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public SideBarConfiguration update(@PathVariable("id") @NotBlank(message = "id is mandatory") String id,
			@Valid @RequestBody SideBarConfiguration request) {
		return service.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_SIDEBARS','ROLE_ADMIN')")
	public void delete(@PathVariable("id") @NotBlank(message = "id is mandatory") String id) {
		service.delete(id);
	}
	
}
