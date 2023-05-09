package com.app.zcustom.repository.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.app.repository.entity.BaseAuditEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "STORE")
public class Store extends BaseAuditEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@NotBlank
	@Size(min = 1, max = 50)
	private String id;
	
	@Size(min = 1, max = 255)
	@Column(name = "NAME", length = 255)
    private String name;
	
	@Column(name = "ENABLED")
    private boolean enabled;
	
	@Size(min = 1, max = 15)
	@Column(name = "VENDOR_NAME", nullable = true, length = 15)
	private String vendorName;
	
	@Size(min = 1, max = 50)
	@Column(name = "VENDOR_STORE_ID", nullable = true, length = 50)
	private String vendorStoreId;
	
	@Size(min = 1, max = 50)
	@Column(name = "HOST", nullable = false, length = 50)
	private String host;
	
	@Size(min = 1, max = 50)
	@Column(name = "USERNAME", nullable = false, length = 50)
	private String username;
	
	@Size(min = 1, max = 50)
	@Column(name = "PASSWORD", nullable = false, length = 50)
	private String password;
	
	@Column(name = "PRODUCT_PRICE_SYNC_ENABLED")
    private boolean productPriceSyncEnabled;
	
	@Pattern(regexp = "RUNNING|NOT_RUNNING|FAILED")
	@Column(name = "PRODUCT_PRICE_SYNC_STATUS")
	private String productPriceSyncStatus;	
	
	@Column(name = "LAST_PRODUCT_PRICE_SYNC_AT", nullable = true)
	private OffsetDateTime lastProductPriceSyncAt;
	
	@Column(name = "INVENTORY_SYNC_ENABLED")
    private boolean inventorySyncEnabled;
	
	@Pattern(regexp = "RUNNING|NOT_RUNNING|FAILED")
	@Column(name = "INVENTORY_SYNC_STATUS")
	private String inventorySyncStatus;
	
	@Column(name = "LAST_INVENTORY_SYNC_AT", nullable = true)
	private OffsetDateTime lastInventorySyncAt;
}
