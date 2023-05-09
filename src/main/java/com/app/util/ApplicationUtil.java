package com.app.util;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.app.exception.CustomApplicationException;
import com.app.repository.entity.SchedulerConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ApplicationUtil {
	
	public static String getJsonStringFromObject(Object obj) {
		ObjectMapper Obj = new ObjectMapper();
		Obj.setSerializationInclusion(Include.NON_NULL);
		Obj.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try { 
            return Obj.writeValueAsString(obj); 
        }  catch (IOException e) { 
            e.printStackTrace(); 
        }
		return null; 
	}
	
	public static String getSource(Device device) {
		StringBuilder source = new StringBuilder();
		if (device.isNormal()) {
			source.append("DESKTOP-");
		} else if (device.isTablet()) {
			source.append("TABLET-");
		} else if (device.isMobile()) {
			source.append("MOBILE-");
		} else {
			source.append("UNKNOWN-");
		}
		
		if (device.getDevicePlatform().equals(DevicePlatform.IOS)) {
			source.append("IOS");
		} else if (device.getDevicePlatform().equals(DevicePlatform.ANDROID)) {
			source.append("ANDROID");
		} else {
			source.append("UNKNOWN");
		}
		return source.toString();
	}
	
	public static String getIp(HttpServletRequest request) {
		return request.getHeader("X-Forwarded-For") == null ? 
				request.getRemoteAddr() : 
					request.getHeader("X-Forwarded-For").split(",")[0];
	}
	
	public static String[] getBasicAuthCredentials() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
		            .getRequest();
			
			// retrieving credentials the HTTP Authorization Header
			String authorizationCredentials = request.getHeader(HttpHeaders.AUTHORIZATION)
					.substring("Basic".length()).trim();

			// decoding credentials
			return new String(Base64.getDecoder().decode(authorizationCredentials)).split(":");

		} catch (Exception e) {
			throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"INTERNAL_SERVER_ERROR", e.getLocalizedMessage());
		}
	}
	
	public static int getNextRetryCount(SchedulerConfiguration sc, int current) {
		return current == sc.getRetryCount() ? current : current+1;
	}
	
	public static OffsetDateTime getNextRunTime(SchedulerConfiguration sc, int currentRetryCount) {
		int factor = sc.getExponentialFactor();
		while (currentRetryCount > 0) {
			factor = factor * factor;
			currentRetryCount--;
		}
		return OffsetDateTime.now().plusSeconds(sc.getNextRunSeconds() 
				+ factor * sc.getNextRunSeconds());
	}
	
}
