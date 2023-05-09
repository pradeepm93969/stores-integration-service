package com.app.zcustom.repository.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.app.repository.entity.BaseScheduledEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "PRODUCT_LINK", uniqueConstraints = {
			@UniqueConstraint(name = "UK_PRODUCT_LINK_RECORD", columnNames = { "PRODUCT_ID", "VENDOR_PRODUCT_ID", "STORE_ID" }),
			@UniqueConstraint(name = "UK_PRODUCT_LINK_STORE_RECORD", columnNames = { "VENDOR_PRODUCT_ID", "STORE_ID" })
		}, indexes = {
			@Index(name="INDEX_PRODUCT_LINK_MAIN", columnList = "VENDOR_PRODUCT_ID,STORE_ID")	
		})
@SequenceGenerator(initialValue = 100, allocationSize = 1, name = "idgen", sequenceName = "PRODUCT_LINK_SEQUENCE")
public class ProductLink extends BaseScheduledEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	private Long id;
	
	@Column(name = "PRODUCT_ID", nullable = false, length = 50)
	@NotBlank
	@Size(min = 1, max = 50)
	private String productId;																																																																																																																																																										
	
	@Column(name = "VENDOR_PRODUCT_ID", nullable = false, length = 50)
	@NotBlank
	@Size(min = 1, max = 50)
	private String vendorProductId;
	
	@Column(name = "STORE_ID", nullable = false, length = 50)
	@NotBlank
	@Size(min = 1, max = 50)
	private String storeId;
	
	@Column(name = "ENABLED")
    private boolean enabled;
	
	@Column(name = "LIST_PRICE")
	private BigDecimal listPrice;
	
	@Column(name = "SALE_PRICE")
	private BigDecimal salePrice;
	
	@PositiveOrZero
	@Column(name = "INVENTORY")
	private Integer inventory;
}
