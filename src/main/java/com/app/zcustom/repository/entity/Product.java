package com.app.zcustom.repository.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.app.repository.entity.BaseAuditEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "product")
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "PRODUCT")
public class Product extends BaseAuditEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@NotBlank
	@Size(min = 1, max = 50)
	private String id;
	
	@Size(min = 3, max = 255)
	@Column(name = "NAME", length = 255)
    private String name;
	
	@Column(name = "ENABLED")
    private boolean enabled;
	
	@Column(name = "LIST_PRICE")
	private BigDecimal listPrice;
}
