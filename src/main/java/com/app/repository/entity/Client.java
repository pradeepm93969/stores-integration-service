package com.app.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "OAUTH_CLIENT_DETAILS")
public class Client extends BaseAuditEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 3, max=255)
	@NotBlank
	@Column(name = "ID", nullable = false, unique = true, length = 255)
	@Pattern(regexp = "^[A-Z]+(?:_[A-Z]+)*$")
	private String id;
	
	@Size(min = 3, max=255)
	@NotBlank(message = "clientSecret is mandatory")
	@Column(name = "CLIENT_SECRET", nullable = false, unique = true, length = 255)
	private String clientSecret;
	
	@Size(max=255)
	@Column(name = "RESOURCE_IDS", length = 255)
	private String resourceIds;
	
	@Size(max=255)
	@Column(name = "SCOPE", length = 255)
	private String scope;
	
	@Size(min = 3, max=255)
	@NotBlank(message = "authorizedGrantTypes is mandatory")
    @Pattern(regexp = "password,refresh_token|client_credentials")
	@Column(name = "AUTHORIZED_GRANT_TYPES", length = 255)
	private String authorizedGrantTypes;
	
	@Size(max=2000)
	@Column(name = "AUTHORITIES", length = 2000)
	private String authorities;
	
	@Min(1)
	@Max(3600)
	@Positive(message = "accessTokenValiditySeconds must be positive")
	@Column(name = "ACCESS_TOKEN_VALIDITY")
	private Integer accessTokenValiditySeconds;
	
	@Min(1)
	@Max(86400)
	@Positive(message = "refreshTokenValiditySeconds must be positive")
	@Column(name = "REFRESH_TOKEN_VALIDITY")
	private Integer refreshTokenValiditySeconds;
	
	@JsonIgnore
	@Column(name = "IS_DEFAULT")
    private boolean isDefault;
	
}
