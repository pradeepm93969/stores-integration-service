package com.app.repository.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.app.annotation.FieldsValueMatch;
import com.app.annotation.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "USER", uniqueConstraints = {
		@UniqueConstraint(name = "UK_USER_MOBILE", columnNames = { "MOBILE_NUMBER", "MOBILE_NUMBER_COUNTRY_CODE" }),
		@UniqueConstraint(name = "UK_USER_EMAIL", columnNames = { "EMAIL" }) })
@SequenceGenerator(initialValue = 100, allocationSize = 1, name = "idgen", sequenceName = "USER_SEQUENCE")
@FieldsValueMatch(field = "username", fieldMatch = "email", message = "username and email must be same")
@ValidPhoneNumber(countryCodeField = "mobileNumberCountryCode", mobileNumberField = "mobileNumber")
public class User extends BaseAuditEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	private Long id;
	
	@Size(min=3, max=255)
    @Column(name = "USERNAME", nullable = false, length = 255)
    private String username;
    
	@JsonIgnore
	@Size(min=3, max=255)
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
	@Size(min=3, max=255)
    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;
    
	@Size(min=3, max=100)
    @Column(name = "FIRST_NAME", nullable = false, length = 100)
    private String firstName;
    
	@Size(min=3, max=100)
    @Column(name = "LAST_NAME", nullable = false, length = 100)
    private String lastName;
	
	@Size(min=3, max=15)
    @Column(name = "AVATAR_IMAGE", nullable = false, length = 10)
    private String avatarImage;
    
    @Column(name = "MOBILE_NUMBER", length = 15)
    private String mobileNumber;
    
    @Min(1)
	@Max(10000)
    @Column(name = "MOBILE_NUMBER_COUNTRY_CODE")
    private int mobileNumberCountryCode;
    
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Column(name = "INVALID_LOGIN_ATTEMPTS")
    private int invalidLoginAttempts;
    
    @Column(name = "INVALID_LOGIN_AT")
    private OffsetDateTime invalidLoginAt;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE", 
    	joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "id")})
    private Set<Role> roles;
    
    @Transient
    private List<String> roleInput;
    
}