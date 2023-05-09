package com.app.zcustom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.zcustom.repository.entity.Store;

public interface StoreRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {
	
	Optional<Store> findByVendorNameAndVendorStoreId(String vendorName, String vendorStoreId);
	
	List<Store> findByVendorNameAndEnabledAndProductPriceSyncEnabled(String vendorName, Boolean enabled, Boolean productSyncEnabled);
	
	List<Store> findByVendorNameAndEnabledAndInventorySyncEnabled(String vendorName, Boolean enabled, Boolean inventorySyncEnabled);

}
