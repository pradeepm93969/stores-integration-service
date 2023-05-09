package com.app.repository.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "ROLE")
public class Role extends BaseAuditEntity {

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

	@JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_PERMISSION", 
    	joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "id")}, 
    	inverseJoinColumns = {@JoinColumn(name = "PERMISSION_ID", referencedColumnName = "id")})
    private Set<Permission> permissions;
    
	@Transient
    private Map<String, List<Permission>> permissionsMap = new HashMap<>();
    
}