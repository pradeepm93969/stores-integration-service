package com.app.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.exception.CustomApplicationException;
import com.app.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtRequestAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try {
			// JWT Token is in the form "Bearer token". Remove Bearer word and
			// get only the Token
			String jwtToken = extractJwtFromRequest(request);

			if (StringUtils.isNotBlank(jwtToken) && jwtUtil.validateToken(jwtToken)) {
				UserDetails userDetails = new User(jwtUtil.getUsernameFromToken(jwtToken), "",
						jwtUtil.getRolesFromToken(jwtToken));

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the
				// Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		} catch (ExpiredJwtException ex) {
			log.error("Token is Expired");
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token has Expired");
		} catch (Exception ex) {
			log.error("Unable to authenticate the token");
			throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"INTERNAL_SERVER_ERROR", ex.getLocalizedMessage());
		}
		chain.doFilter(request, response);
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
}