package com.app.service;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.app.repository.entity.ApiConfiguration;
import com.app.repository.entity.IntegrationLog;
import com.app.util.ApplicationUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class IntegrationService {
	
	@Autowired
	private ApiConfigurationService apiConfigurationService;
	
	@Autowired
	private IntegrationLogService integrationLogService;
	
	public HttpHeaders getHeaders(String apiConfigurationName) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ApiConfiguration configuration = apiConfigurationService.getById(apiConfigurationName);
		if (configuration != null) {
			if ("BASIC".equalsIgnoreCase(configuration.getAuthenticationType())) {
				headers.add("Authorization", "Basic " + getBase64Credentials(configuration.getUsername(), configuration.getPassword()));
			} else if ("BEARER".equalsIgnoreCase(configuration.getAuthenticationType())) {
				//headers.add("Authorization", "Bearer " + configuration.getCustomValue());
			} else if ("CUSTOM".equalsIgnoreCase(configuration.getAuthenticationType())) {
				headers.add(configuration.getCustomHeader(), configuration.getCustomValue());
			}
		}
		return headers;
	}
	
	public <T, R> ResponseEntity<T> getResponse(String apiConfigurationName, HttpHeaders httpHeaders, Map<String, String> urlParameters, 
			R request, Class<T> responseClass, IntegrationLog integrationLog) {
		
		ResponseEntity<T> responseEntity = null;
		int errorCode = 0;
		String errorDesc = "";
		ApiConfiguration configuration = null;
		String url = "";
		long StartTime = System.currentTimeMillis();
		long timeTaken = 0;
		boolean eligibleForAudit = false;
		
		log.info("IntegrationService.getResponse() :: apiConfiguration=" + apiConfigurationName + " request:" + 
				ApplicationUtil.getJsonStringFromObject(request));
		
		try {
			
			//Get the API Configuration
			configuration = apiConfigurationService.getById(apiConfigurationName);
			
			//Get the URL
			url = configuration.getUrl();
			if (urlParameters != null) {
				StringSubstitutor sub = new StringSubstitutor(urlParameters);
				url = sub.replace(url);
			}
			
			//Get Rest Template
			RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(configuration.getTimeoutInSeconds());
			
			//Construct Http Entity
			HttpEntity<R> entity =  null;
			if (HttpMethod.GET.toString().equalsIgnoreCase(configuration.getMethod())) {
				entity = new HttpEntity<R>(httpHeaders);
			} else {
				entity = new HttpEntity<R>(request, httpHeaders);
			}
			
			eligibleForAudit = true;
			//Integration Call
			StartTime = System.currentTimeMillis();
			responseEntity = restTemplate.exchange(
					url, HttpMethod.valueOf(configuration.getMethod().toString()), entity, responseClass);
			timeTaken = (System.currentTimeMillis() - StartTime);
			
			log.info("IntegrationService.getResponse() :: apiConfiguration=" + apiConfigurationName + " request:" + 
					ApplicationUtil.getJsonStringFromObject(request) + " Successfully Called");
			
			return responseEntity;
		} catch(Exception e) {
			errorDesc = e.getMessage();
			errorCode = (e instanceof HttpClientErrorException) ? ((HttpClientErrorException) e).getRawStatusCode() : 999;
			throw e;
		} finally {
			
			if (eligibleForAudit) {
				
				//Audit to Database
				integrationLog.setId(null);
				integrationLog.setUrl(url);
				integrationLog.setMethod(configuration.getMethod());
				integrationLog.setResponseStatus((errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode));
				integrationLog.setResponseTime(timeTaken);
				integrationLog.setRequest(ApplicationUtil.getJsonStringFromObject(request).getBytes());
				integrationLog.setResponse((errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc).getBytes());
				integrationLog.setSystemName(configuration.getSystemName());
				integrationLogService.save(integrationLog);
				
				//Audit to Log
				StringBuilder builder = new StringBuilder();
				builder.append("\n");
				builder.append("################################################################################" + "\n");
				builder.append("IntegrationService.getResponse() - Start Time: " + new Date(StartTime) + "\n");
				builder.append("Time Taken: " + timeTaken + "\n");
				builder.append("URL: " + url + " [" + (configuration == null ? "" : configuration.getMethod()) + "]\n");
				builder.append("Request: " + ApplicationUtil.getJsonStringFromObject(request) + "\n");
				builder.append("Status: " + (errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode) + "\n");
				builder.append("Response: " + (errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc) + "\n");
				builder.append("################################################################################");
				
				log.info(builder.toString());
			}
		}
		
	}
	
	public static String getBase64Credentials(String username, String password) {
		StringBuffer plainCreds = new StringBuffer();
		plainCreds.append(username);
		plainCreds.append(":");
		plainCreds.append(password);
		byte[] plainCredsBytes = plainCreds.toString().getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	

}
