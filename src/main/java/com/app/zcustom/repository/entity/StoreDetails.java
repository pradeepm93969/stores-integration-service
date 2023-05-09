package com.app.zcustom.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.app.repository.entity.BaseAuditEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "STORE_DETAILS", uniqueConstraints = {
		@UniqueConstraint(name = "UK_STORE_MOBILE", columnNames = { "MOBILE_NUMBER",
				"MOBILE_NUMBER_COUNTRY_CODE" }),
		@UniqueConstraint(name = "UK_STORE_EMAIL", columnNames = { "EMAIL" }) })
public class StoreDetails extends BaseAuditEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@NotBlank
	@Size(min = 3, max = 50)
	private String id;
	
	@Column(name = "MOBILE_NUMBER", length = 15)
    private String mobileNumber;
    
    @Min(1)
	@Max(10000)
    @Column(name = "MOBILE_NUMBER_COUNTRY_CODE")
    private int mobileNumberCountryCode;
    
    @Size(min=3, max=255)
    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;
    
    @Size(min = 3, max = 255)
	@NotBlank
    @Column(name = "ADDRESS_LINE1", nullable = false, length = 255)
	private String addressLine1;

    @Size(max = 255)
	@Column(name = "ADDRESS_LINE2", length = 255)
	private String addressLine2;

    @Size(min = 3, max = 255)
	@NotBlank
	@Column(name = "CITY", nullable = false, length = 255)
	private String city;

    @Size(min = 3, max = 255)
	@NotBlank
	@Column(name = "STATE", nullable = false, length = 255)
	private String state;

    @Size(min = 3, max = 255)
	@NotBlank
	@Column(name = "COUNTRY", nullable = false, length = 255)
	private String country;

	@Column(name = "LATITUDE", nullable = false)
	private Double latitude;

	@Column(name = "LONGITUDE", nullable = false)
	private Double longitude;
	
    
}
