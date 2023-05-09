package com.app.repository.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "SIDE_BAR_CONFIGURATION")
public class SideBarConfiguration extends BaseAuditEntity {
	
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
	
	@Size(max = 50)
	@Column(name = "ICON", length = 20)
    private String icon;
	
	@Size(max = 50)
	@Column(name = "PARENT_ID", length = 20)
    private String parentId;
	
	@Size(max = 200)
	@Column(name = "LINK", length = 200)
    private String link;
	
    @Column(name = "ENABLED")
    private boolean enabled;

    @Column(name = "HEADER")
    private boolean header;
    
    @Column(name = "ROOT")
    private boolean root;
    
    @Column(name = "SEQUENCE")
    private int sequence;
    
	@Size(max=2000)
	@Column(name = "AUTHORITIES", length = 2000)
	private String authorities;
    
	@Transient
    private List<SideBarConfiguration> childs = new ArrayList<>();

}
