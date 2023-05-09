package com.app.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.SmsScheduled;

public interface SmsScheduledRepository extends JpaRepository<SmsScheduled, Long>, JpaSpecificationExecutor<SmsScheduled> {
	
	long countByProcessedAndRetryCountLessThanAndNextRunAtBefore(boolean processed,
			int maxRetryCount, OffsetDateTime currentTime);
	
	Page<SmsScheduled> findByProcessedAndRetryCountLessThanAndNextRunAtBefore(boolean processed,
			int maxRetryCount, OffsetDateTime currentTime, Pageable pageable);

}
