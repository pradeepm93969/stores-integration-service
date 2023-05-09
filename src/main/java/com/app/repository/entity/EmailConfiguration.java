package com.app.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "EMAIL_CONFIGURATION")
public class EmailConfiguration extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 3, max=50)
	@NotBlank
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
	
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Size(min=3, max=255)
    @Column(name = "SUBJECT", nullable = false, length = 255)
    private String subject;
    
    @Size(min=3, max=4000)
    @Column(name = "BODY", nullable = false, length = 4000)
    private String body;
    
    @Size(max=150)
    @Column(name = "ALLOWED_DOMAIN", length = 150)
    private String allowedDomain;

}
