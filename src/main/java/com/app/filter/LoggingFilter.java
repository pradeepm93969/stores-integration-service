package com.app.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LoggingFilter extends GenericFilterBean {
	
	public static boolean enableLogging = true;
	
	public static Set<String> urlsToSkip = new HashSet<>(Arrays.asList(
			"swagger", "actuator", "api-docs", "/webjars/", "/css/", "/img/","/js/","favicon.ico", "public", "auth", "password"));
	
	public static boolean urlToBeSkipped(String url) {
		for (String skippedUrl : urlsToSkip) {
			if (url.contains(skippedUrl)) {
				return true;
			}
		}
		return false;
	}
    /**
     * It's important that you actually register your filter this way rather then just annotating it
     * as @Component as you need to be able to set for which "DispatcherType"s to enable the filter
     * (see point *1*)
     * 
     * @return
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> initFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());

        // *1* make sure you sett all dispatcher types if you want the filter to log upon
        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

        // *2* this should put your filter above any other filter
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	
    	if (!urlToBeSkipped(((HttpServletRequest) request).getRequestURL().toString())) {
	        ContentCachingRequestWrapper wreq = new ContentCachingRequestWrapper((HttpServletRequest) request);
	        ContentCachingResponseWrapper wres = new ContentCachingResponseWrapper((HttpServletResponse) response);
	        if (!wreq.getRequestURI().contains("error")) {
	        	wreq.setAttribute("UNIQUE_REQUEST_ID", System.currentTimeMillis() + UUID.randomUUID().toString());
	        }
	        try {

	            // let it be ...
	        	chain.doFilter(wreq, wres);

	        } catch (Throwable t) {
	            throw t;
	        } finally {
	        	
	        	 while (wreq.getInputStream().read() >= 0);
	        	 
	        	 if (enableLogging) {
		        	 String body = new String(wreq.getContentAsByteArray());
		        	 String responseBody = new String(wres.getContentAsByteArray());
		        	 
		        	 ObjectMapper mapper = new ObjectMapper();
		        	 JsonNode tree = mapper.createObjectNode();
		        	 try {
		        		 tree = mapper.readTree(body);
		        	 } catch (Exception e) {}
		
		             StringBuilder builder = new StringBuilder();
		             builder.append("\n");
		             builder.append("###############################################################").append("\n");
		             builder.append("[").append(wreq.getMethod()).append("] ").append(wreq.getRequestURI().toString())
		             	.append(StringUtils.isBlank(wreq.getQueryString()) ? "" : "?" + wreq.getQueryString()).append("\n");
		             builder.append("HEADERS: ").append("UNIQUE_REQUEST_ID=").append(wreq.getAttribute("UNIQUE_REQUEST_ID"))
		             	.append("       ,Authorization=").append(wreq.getHeader("Authorization")).append("\n");
		             builder.append("Remote Address: ").append(wreq.getRemoteAddr()).append("\n");
		             builder.append("Request Body: ").append(tree.toString()).append("\n");
		             builder.append("Response Status Code: ").append(wres.getStatus()).append("\n");
		             builder.append("Response Body: ").append(responseBody).append("\n");
		             builder.append("###############################################################").append("\n");
		             if (responseBody != null && responseBody.trim().startsWith("<!DOCTYPE HTML>")) {
		            	 //Custom pages
		             } else if (responseBody != null && responseBody.trim().startsWith("<html>")) {
		            	//OOTB pages
		             } else if (responseBody != null && responseBody.trim().startsWith("<div")) {
			            	//OOTB pages
			         } else {
		            	 log.info(builder.toString());
		             }
		             wres.copyBodyToResponse();
	        	 }

	             // One more point, in case of redirect this will be called twice! beware to handle that
	             // somewhat
	        }
    	} else {
    		 chain.doFilter(request, response);
    	}
    	
        
    }
}