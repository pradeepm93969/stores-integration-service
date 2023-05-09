package com.app.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {
	
	private static Map<Integer, RestTemplate> timeoutToTemplateMap =
	          new ConcurrentHashMap<>();
	
	public static RestTemplate getRestTemplate(Integer readTimeout){
	   return timeoutToTemplateMap.computeIfAbsent(readTimeout, 
	                        key-> {
	                           HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	            			   factory.setReadTimeout(readTimeout * 1000);
	            			   return new RestTemplate(factory);
	                        });
	}

}
