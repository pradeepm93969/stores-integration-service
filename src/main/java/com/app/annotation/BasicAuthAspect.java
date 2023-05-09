package com.app.annotation;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.app.exception.CustomApplicationException;
import com.app.repository.entity.Client;
import com.app.service.ClientService;
import com.app.util.ApplicationUtil;

@Aspect
@Component
public class BasicAuthAspect {

	@Autowired
	private ClientService clientService;

	@Before("@annotation(com.app.annotation.BasicAuth)")
	public void checkClientHasAccess(JoinPoint jp) throws Throwable {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();

			// retrieving credentials the HTTP Authorization Header
			if (request.getHeader(HttpHeaders.AUTHORIZATION) == null
					|| !request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Basic"))
				throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access");

			//Validate if Client has Authorized Grant type
			String[] credentials = ApplicationUtil.getBasicAuthCredentials();
	    	Client client = clientService.getById(credentials[0]);
	    	if (!credentials[1].equals(client.getClientSecret())) {
	    		throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access");
	    	}

			BasicAuth basicAuth = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(BasicAuth.class);
			List<String> methodAuthorities = Arrays.asList(basicAuth.permissions().split(","));

			String clientAuthority = client.getAuthorities();

			if (StringUtils.isNotBlank(clientAuthority)) {
				List<String> clientAuthorities = Arrays.asList(clientAuthority.split(","));
				for (String methodAuthority : methodAuthorities) {
					if (clientAuthorities.contains(methodAuthority)) {
						return;
					}
				}
			}

			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "UNAUTHENTICATED");

		} catch (CustomApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
					e.getLocalizedMessage());
		}
	}
}
