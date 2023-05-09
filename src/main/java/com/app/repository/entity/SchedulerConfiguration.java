package com.app.repository.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "SCHEDULER_CONFIGURATION")
public class SchedulerConfiguration extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 3, max=50)
	@NotBlank
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
    
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Size(min=3, max=10)
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status;
    
    @Max(25)
    @Column(name = "THREAD_COUNT")
    private int threadCount;
    
    @Max(10)
    @Column(name = "RETRY_COUNT")
    private int retryCount;
    
    @Max(250)
    @Column(name = "BATCH_SIZE")
    private int batchSize;
    
    @Column(name = "RESTART_SECONDS")
    private int restartSeconds;
    
    @Max(1000)
    @Column(name = "EXPONENTIAL_FACTOR")
    private int exponentialFactor;
    
    @Max(86400)
    @Column(name = "NEXT_RUN_SECONDS")
    private int nextRunSeconds;
    
    @Column(name = "DEBUG")
    private boolean debug;
    
    @Column(name = "LAST_SUCCESSFUL_START_AT")
    private OffsetDateTime lastSuccessfulStartAt;
    
    @Column(name = "LAST_SUCCESSFUL_END_AT")
    private OffsetDateTime lastSuccessfulEndAt;
}
