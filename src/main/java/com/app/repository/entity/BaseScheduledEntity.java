package com.app.repository.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
public abstract class BaseScheduledEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@CreatedDate
	@Column(name = "CREATED_AT", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
	
	@LastModifiedDate
	@Column(name = "UPDATED_AT", nullable = false)
	private OffsetDateTime updatedAt;
	
	@CreatedBy
	@Column(name = "CREATED_BY", nullable = false, updatable = false, length = 255)
	private String createdBy;
	
	@LastModifiedBy
	@Column(name = "UPDATED_BY", nullable = false, length = 255)
	private String updatedBy;
	
	@Column(name = "PROCESSED")
    private boolean processed;
	
	@Column(name = "RETRY_COUNT")
	private int retryCount;
	
	@Column(name = "NEXT_RUN_AT", nullable = false)
	private OffsetDateTime nextRunAt = OffsetDateTime.now();
    
    @Column(name = "API_ERROR", length = 2000)
    private String apiError;

}
