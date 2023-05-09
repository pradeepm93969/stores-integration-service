package com.app.repository.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "INTEGRATION_LOG")
public class IntegrationLog extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	private Long id;
	
	@Column(name = "IDENTIFIER", length = 250)
	private String identifier;
    
    @Column(name = "SUB_IDENTIFIER", length = 250)
    private String subIdentifier;
    
    @Column(name = "OPERATION_TYPE", length = 250)
    private String operationType;
    
    @Column(name = "URL", nullable = false, length = 2000)
    private String url;
    
    @Column(name = "METHOD", nullable = false, length = 10)
    private String method;
    
    @Basic(fetch=FetchType.LAZY)
    @Lob
    @Column(name = "REQUEST", nullable = true)
    private byte[] request;
    
    @Basic(fetch=FetchType.LAZY)
    @Lob
    @Column(name = "RESPONSE", nullable = true)
    private byte[] response;
    
    @Column(name = "RESPONSE_TIME")
    private Long responseTime;
    
    @Column(name = "RESPONSE_STATUS")
    private int responseStatus; 
    
    @Column(name = "SYSTEM_NAME", nullable = false, length = 50)
    private String systemName;
}
