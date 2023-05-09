package com.app.zcustom.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.exception.CustomApplicationException;
import com.app.zcustom.repository.StoreRepository;
import com.app.zcustom.repository.entity.Store;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoreService {
	
	@Autowired
	private StoreRepository repository;
	
	public Page<Store> get(Specification<Store> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<Store> get(Specification<Store> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}
	
	@Cacheable(value = "Store", key = "#id")
	public Store getById(String id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", "Entity not found"));
	}
	
	public Store create(Store request) {
		Optional<Store> checkOptionalEntity = repository.findById(request.getId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "ENTITY_ID_TAKEN", "Entity ID is taken");
		}
		checkOptionalEntity = repository.findByVendorNameAndVendorStoreId(request.getVendorName(), request.getVendorStoreId());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "STORE_EXISTS", "Vendor Store already exists");
		}
		return repository.save(request);
	}

	@CachePut(value = "Store", key = "#id")
	public Store update(String id, Store request) {
		getById(id);
		Optional<Store> checkOptionalEntity = repository.findByVendorNameAndVendorStoreId(request.getVendorName(), request.getVendorStoreId());
		if (checkOptionalEntity.isPresent() && !checkOptionalEntity.get().getId().equals(id)) {
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "STORE_EXISTS", "Vendor Store already exists");
		}
		log.info("Store ID is good to create:" + request.getId());
		request.setId(id);
		return repository.save(request);
	}

	@CacheEvict(value = "Store", key = "#id")
	public void delete(String id) {
		getById(id);
		repository.deleteById(id);
	}
	
	public List<Store> getStoresByVendorNameForProductPriceSync(
			String vendorName, boolean enabled, boolean productSyncEnabled) {
		return repository.findByVendorNameAndEnabledAndProductPriceSyncEnabled(
				vendorName, enabled, productSyncEnabled);
	}
	
	public List<Store> getStoresByVendorNameForInventorySync(
			String vendorName, boolean enabled, boolean inventorySyncEnabled) {
		return repository.findByVendorNameAndEnabledAndInventorySyncEnabled(
				vendorName, enabled, inventorySyncEnabled);
	}
	
	public List<Store> getStoresByIds(List<String> storeIds) {
		return repository.findAllById(storeIds);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void productPriceSyncStart(Store store) {
		store.setProductPriceSyncStatus("RUNNING");
		repository.save(store);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void productPriceSyncFailed(Store store) {
		store.setProductPriceSyncStatus("FAILED");
		repository.save(store);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void productPriceSyncFinished(Store store) {
		store.setProductPriceSyncStatus("NOT_RUNNING");
		store.setLastProductPriceSyncAt(OffsetDateTime.now());
		repository.save(store);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inventorySyncStart(Store store) {
		store.setInventorySyncStatus("RUNNING");
		repository.save(store);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inventorySyncFailed(Store store) {
		store.setInventorySyncStatus("FAILED");
		repository.save(store);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inventorySyncFinished(Store store) {
		store.setInventorySyncStatus("NOT_RUNNING");
		store.setLastInventorySyncAt(OffsetDateTime.now());
		repository.save(store);
    }

}
