package com.app.util;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.app.exception.CustomApplicationException;
import com.app.modal.response.TokenResponse;
import com.app.repository.entity.Client;
import com.app.repository.entity.Permission;
import com.app.repository.entity.Role;
import com.app.repository.entity.User;
import com.app.repository.entity.enums.JwtTokenTypeEnum;
import com.app.service.RoleService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUtil {
	
	private static String secretKey;
	
	@SuppressWarnings("unused")
	private static String publicKey;
	
	@Autowired
	private RoleService roleService;
	
	private static final String KEY_ALIAS = "jwt";
	private static final String KEY_PASSWORD = "password";
	
	public JwtUtil() {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load((new ClassPathResource("jwt.jks")).getInputStream(), KEY_PASSWORD.toCharArray());
			
			X509Certificate certificate = (X509Certificate)keyStore.getCertificate("jwt");
			publicKey = Base64.getMimeEncoder().encodeToString(certificate.getPublicKey().getEncoded());
			
			KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS,
		        new KeyStore.PasswordProtection(KEY_PASSWORD.toCharArray())
		    );
			secretKey = Base64.getMimeEncoder().encodeToString(privateKeyEntry.getPrivateKey().getEncoded());
			
		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | UnrecoverableEntryException e) {
			log.error(e.getLocalizedMessage());
			System.exit(1);
		}
	}
	
	public String generateToken(User user, Client client, JwtTokenTypeEnum jwtTokenType) {
		Map<String, Object> claims = new HashMap<>();
		
		if (user != null) {
			claims.put("id", Long.toString(user.getId()));
			claims.put("firstName", user.getFirstName());
			claims.put("avatarImage", user.getAvatarImage());
			claims.put("roles", user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toSet()));
		} else {
			claims.put("id", client.getId());
			claims.put("roles", getAllAutorities(client.getAuthorities()));
		}
		claims.put("jwtTokenType", jwtTokenType.toString());
		
		int expiryTimeInSeconds = (JwtTokenTypeEnum.REFRESH_TOKEN == jwtTokenType) ? 
				client.getRefreshTokenValiditySeconds() : client.getAccessTokenValiditySeconds();
		
		return Jwts.builder().setClaims(claims).setSubject(user.getEmail()).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * expiryTimeInSeconds))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}
	
	public List<String> getAllAutorities(String authorities) {
		List<String> finalAuthorities = new ArrayList<String>();
		if (StringUtils.isNotBlank(authorities)) {
			String[] initalAuthorities = authorities.split(",");
			//Optional<Role> role = null;
			for (int i = 0; i < initalAuthorities.length; i++) {
				finalAuthorities.add(initalAuthorities[i]);
			}
		}
		return finalAuthorities;
	}
	
	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthorized Access");
		} catch (ExpiredJwtException ex) {
			throw ex;
		}
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		List<String> userRoles = (List<String>) claims.get("roles");
		List<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
		if (userRoles != null && userRoles.size() > 0) {
			Set<Permission> permissions = null;
			Role role = null;
			for (String userRole : userRoles) {
				role = roleService.getById(userRole);
				if (role != null) {
					roles.add(new SimpleGrantedAuthority(userRole.startsWith("ROLE_") ? userRole : 
						"ROLE_" + userRole));
					permissions = role.getPermissions();
					for (Permission permission : permissions) {
						roles.add(new SimpleGrantedAuthority(permission.getId().startsWith("ROLE_") ? 
								permission.getId() : "ROLE_" + permission.getId()));
					}
				}
			}
		}
		return roles;
	}
	
	public String getTypeFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return (String) claims.get("jwtTokenType");
	}
	
	public String getUserIdFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return (String) claims.get("id");
	}
	
	public String getFirstNameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return (String) claims.get("firstName");
	}
	
	public String extractJwtFromRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	            .getRequest();
		
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public TokenResponse getTokenResponse(String token) {
		TokenResponse response = new TokenResponse();
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		response.setUserId(Long.parseLong((String) claims.get("id")));
		response.setTokenType((String) claims.get("jwtTokenType"));
		response.setAvatarImage((String) claims.get("avatarImage"));
		response.setFirstName((String) claims.get("firstName"));
		response.setRoles((List<String>) claims.get("roles"));
		return response;
	}

}
