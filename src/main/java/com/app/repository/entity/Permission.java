package com.app.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "PERMISSION")
public class Permission extends BaseAuditEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 3, max=50)
	@NotBlank
	@Column(name = "ID", nullable = false, unique = true, length = 50)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
	
	@Size(min = 3, max = 50)
	@NotBlank(message = "Name is mandatory")
	@Column(name = "NAME", nullable = false, length = 50)
    private String name;
	
	@Size(min = 3, max = 50)
	@NotBlank(message = "Group is mandatory")
	@Column(name = "PERMISSION_GROUP", nullable = false, length = 50)
    private String group;
	
	@Transient
	private boolean enabled;
	
}
