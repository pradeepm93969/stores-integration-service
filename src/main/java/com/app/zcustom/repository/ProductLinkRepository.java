package com.app.zcustom.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.zcustom.repository.entity.ProductLink;

public interface ProductLinkRepository extends JpaRepository<ProductLink, Long>, JpaSpecificationExecutor<ProductLink> {

	ProductLink findByStoreIdAndVendorProductId(String storeId, String vendorProductId);

	long countByProcessedAndRetryCountLessThanAndNextRunAtBefore(boolean processed, int retryCount, OffsetDateTime nextRunAt);

	Page<ProductLink> findByProcessedAndRetryCountLessThanAndNextRunAtBefore(boolean processed, int retryCount,
			OffsetDateTime currentTime, Pageable pageable);
	
}
