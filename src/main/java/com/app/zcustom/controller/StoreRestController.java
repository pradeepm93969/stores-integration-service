package com.app.zcustom.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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

import com.app.service.CsvService;
import com.app.zcustom.repository.entity.Store;
import com.app.zcustom.service.StoreService;
import com.app.zcustom.service.StoreSyncService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "4. Store Configurations")
@SecurityRequirement(name = "bearerAuth")
public class StoreRestController {
	
	@Autowired
	private StoreService service;
	
	@Autowired
	private StoreSyncService storeSyncService;
	
	@Autowired
	private CsvService csvService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_READ_STORES','ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public Page<Store> get(
			@And({
				@Spec(path = "id", params = "id", spec = Equal.class),
                @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "vendorName", params = "vendorName", spec = Equal.class),
                @Spec(path = "vendorStoreId", params = "vendorStoreId", spec = LikeIgnoreCase.class),
                @Spec(path = "productPriceSyncEnabled", params = "productPriceSyncEnabled", spec = Equal.class),
                @Spec(path = "inventorySyncEnabled", params = "inventorySyncEnabled", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<Store> spec,
			
			@Positive @RequestParam(defaultValue = "1") Integer pageNo, 
			@Positive @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
		return service.get(spec, pageNo-1, pageSize, sortBy, sortDirection);
	}
	
	@GetMapping("/extract")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_STORES','ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public ResponseEntity<Resource> extract(
			@And({
				@Spec(path = "id", params = "id", spec = Equal.class),
                @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                @Spec(path = "enabled", params = "enabled", spec = Equal.class),
                @Spec(path = "vendorName", params = "vendorName", spec = Equal.class),
                @Spec(path = "vendorStoreId", params = "vendorStoreId", spec = LikeIgnoreCase.class),
                @Spec(path = "productPriceSyncEnabled", params = "productPriceSyncEnabled", spec = Equal.class),
                @Spec(path = "inventorySyncEnabled", params = "inventorySyncEnabled", spec = Equal.class),
                @Spec(path = "createdAt", params = {"createdAtGt", "createdAtLt"}, spec = Between.class)
			}) Specification<Store> spec,
			
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) throws IOException {
		List<Store> clients = service.get(spec, sortBy, sortDirection);
		
		List<String> noNeededColumn = Arrays.asList(new String[] { "createdAt", "createdBy", "updatedAt", "updatedBy" });
		Resource resource = csvService.generateCsvFile(clients, noNeededColumn);

        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"stores_" + now + ".csv\"")
                             .body(resource);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_READ_STORES','ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public Store getById(@PathVariable("id") 
		@NotBlank(message = "id is mandatory") String id) {
		return service.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public Store create(@Valid @RequestBody Store request) {
		return service.create(request);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public Store update(@PathVariable("id") @NotBlank(message = "id is mandatory") String id,
			@Valid @RequestBody Store request) {
		return service.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public void delete(@PathVariable("id") @NotBlank(message = "id is mandatory") String id) {
		service.delete(id);
	}
	
	@PostMapping("syncPrice")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public void syncPrice(@RequestBody @NotEmpty List<String> storeIds) {
		Thread thread = new Thread(){
		    public void run(){
		    	storeSyncService.fetchPriceFromVendor(storeIds);
		    }
		};
		thread.start();
	}
	
	@PostMapping("syncInventory")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGE_STORES','ROLE_ADMIN')")
	public void syncInventory(@RequestBody @NotEmpty List<String> storeIds) {
		Thread thread = new Thread(){
		    public void run(){
		    	storeSyncService.fetchInventoryFromVendor(storeIds);
		    }
		};
		thread.start();
	}

}
