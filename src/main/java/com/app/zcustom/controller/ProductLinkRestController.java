package com.app.zcustom.controller;

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
import org.springframework.web.multipart.MultipartFile;

import com.app.service.CsvService;
import com.app.zcustom.repository.entity.ProductLink;
import com.app.zcustom.service.ProductLinkService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/productLinks")
@Tag(name = "6. Product Link Configurations")
@SecurityRequirement(name = "bearerAuth")
public class ProductLinkRestController {
	
	@Autowired
	private ProductLinkService service;
	
	@Autowired
	private CsvService csvService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_PRODUCT_LINKS','ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public Page<ProductLink> get(
			@And({
				@Spec(path = "productId", params = "productId", spec = Equal.class),
                @Spec(path = "vendorProductId", params = "vendorProductId", spec = Equal.class),
                @Spec(path = "storeId", params = "storeId", spec = Equal.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "processed", params = "processed", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<ProductLink> spec,
			
			@Positive @RequestParam(defaultValue = "1") Integer pageNo, 
			@Positive @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
		return service.get(spec, pageNo-1, pageSize, sortBy, sortDirection);
	}
	
	@GetMapping("/extract")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_PRODUCT_LINKS','ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public ResponseEntity<Resource> extract(
			@And({
				@Spec(path = "productId", params = "productId", spec = Equal.class),
                @Spec(path = "vendorProductId", params = "vendorProductId", spec = Equal.class),
                @Spec(path = "storeId", params = "storeId", spec = Equal.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "processed", params = "processed", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<ProductLink> spec,
			
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) throws IOException {
		List<ProductLink> clients = service.get(spec, sortBy, sortDirection);
		
		List<String> noNeededColumn = Arrays.asList(new String[] { "createdAt", "createdBy", "updatedAt", "updatedBy", "apiError", "nextRunAt", "processed", "retryCount"});
		Resource resource = csvService.generateCsvFile(clients, noNeededColumn);

        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products_" + now + ".csv\"")
                             .body(resource);
	}
	
	@PostMapping("/upload")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public void uploadFile(@RequestParam("file") MultipartFile file) {
		service.upload(file);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_PRODUCT_LINKS','ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public ProductLink getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") Long id) {
		return service.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public ProductLink create(@Valid @RequestBody ProductLink request) {
		return service.create(request);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public ProductLink update(@PathVariable("id") @NotBlank(message = "id is mandatory") Long id,
			@Valid @RequestBody ProductLink request) {
		return service.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_PRODUCT_LINKS','ROLE_ADMIN')")
	public void delete(@PathVariable("id") @NotBlank(message = "id is mandatory") Long id) {
		service.delete(id);
	}

}
