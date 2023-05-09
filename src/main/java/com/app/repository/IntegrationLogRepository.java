package com.app.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.app.repository.entity.IntegrationLog;

public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long>, JpaSpecificationExecutor<IntegrationLog> {
	
	List<IntegrationLog> findByCreatedAtBefore(OffsetDateTime twoWeeks);
	
	@Modifying
	@Transactional
	@Query(value = "delete from INTEGRATION_LOG i where i.CREATED_AT <= ?1", nativeQuery = true)
	void deleteRecords(String before);

}
