package com.app.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "API_CONFIGURATION")
public class ApiConfiguration extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@NotBlank
	@Size(min = 3, max = 50)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
    
	@Size(min = 3, max=2000)
    @Column(name = "URL", nullable = false, length = 2000)
    private String url;
    
	@Pattern(regexp="POST|GET|PUT|DELETE", message="method must be either POST|GET|PUT|DELETE")
	@Size(min = 3, max=10)
    @Column(name = "METHOD", nullable = false, length = 10)
    private String method;
	
	@Pattern(regexp="BASIC|BEARER|CUSTOM", message="authenticationType must be either BASIC|BEARER|CUSTOM")
	@Size(min = 3, max=20)
    @Column(name = "AUTHENTICATION_TYPE", nullable = false, length = 20)
    private String authenticationType;
    
	@Size(max=150)
    @Column(name = "USERNAME", nullable = true, length = 150)
    private String username;
    
	@Size(max=150)
    @Column(name = "PASSWORD", nullable = true, length = 150)
    private String password;
    
	@Size(max=2000)
    @Column(name = "CUSTOM_VALUE", nullable = true, length = 2000)
    private String customValue;
    
	@Size(max=20)
    @Column(name = "CUSTOM_HEADER", nullable = true, length = 20)
    private String customHeader;
    
	@Min(1)
	@Max(600)
    @Column(name = "TIMEOUT_IN_SECONDS")
    private int timeoutInSeconds;
    
	@Size(min = 3, max=50)
    @Column(name = "SYSTEM_NAME", nullable = false, length = 50)
    private String systemName;

}
