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
@Table(name = "SMS_CONFIGURATION")
public class SmsConfiguration extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 3, max=50)
	@NotBlank
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
	
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Size(min=3, max=1000)
    @Column(name = "MESSAGE", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "WHITELISTED")
    private boolean whitelisted;
    
    @Size(max=4000)
    @Column(name = "WHITELISTED_NUMBERS", length = 4000)
    private String whitelistedNumbers;

}
