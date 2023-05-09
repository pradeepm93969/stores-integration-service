package com.app.zcustom.integration;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.app.repository.entity.IntegrationLog;
import com.app.service.IntegrationLogService;
import com.app.service.RestTemplateFactory;
import com.app.util.ApplicationUtil;
import com.app.zcustom.modal.response.lumensoft.InventoryResponse;
import com.app.zcustom.modal.response.lumensoft.ProductsResponse;
import com.app.zcustom.repository.entity.Store;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LumensoftRestService {
	
	@Autowired
	private IntegrationLogService integrationLogService;
	
	public HttpHeaders getHeaders(Store store) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("AppID", store.getUsername());
		headers.add("AppKey", store.getPassword());
		return headers;
	}
	
	public ProductsResponse getProductsV2Response(Store store, int offset) {
		
		ResponseEntity<ProductsResponse> responseEntity = null;
		int errorCode = 0;
		String errorDesc = "";
		String url = "";
		long StartTime = System.currentTimeMillis();
		long timeTaken = 0;
		boolean eligibleForAudit = true;
		
		log.info("LumensoftRestService.getProductsV2Response() :: store=" + store.getId() 
			+ ", offset=" + offset);
		
		try {
			url = store.getHost() + "/api/V2/Products/?Take=200&Skip=" + offset;
			if (store.getLastProductPriceSyncAt() != null) {
				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				url += "&TimeStamp=" + fmt.format(store.getLastProductPriceSyncAt());
			}
			
			//Get Rest Template
			RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(10);
			
			//Construct Http Entity
			HttpEntity<?> entity = new HttpEntity<>(getHeaders(store));
			
			eligibleForAudit = true;
			//Integration Call
			StartTime = System.currentTimeMillis();
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, ProductsResponse.class);
			timeTaken = (System.currentTimeMillis() - StartTime);
			
			log.info("IntegrationService.getResponse() :: status=" + responseEntity.getStatusCodeValue());
			
			return responseEntity.getBody();
		} catch(Exception e) {
			errorDesc = e.getMessage();
			errorCode = (e instanceof HttpClientErrorException) ? ((HttpClientErrorException) e).getRawStatusCode() : 999;
			throw e;
		} finally {
			
			if (eligibleForAudit) {
				
				//Audit to Database
				IntegrationLog integrationLog = new IntegrationLog();
				integrationLog.setIdentifier(store.getId());
				integrationLog.setSubIdentifier("Offset:" + offset);
				integrationLog.setOperationType("LUMENSOFT_INBOUND_PRODUCT");
				integrationLog.setId(null);
				integrationLog.setUrl(url);
				integrationLog.setMethod("GET");
				integrationLog.setResponseStatus((errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode));
				integrationLog.setResponseTime(timeTaken);
				integrationLog.setRequest(null);
				integrationLog.setResponse((errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc).getBytes());
				integrationLog.setSystemName("LUMENSOFT");
				integrationLogService.save(integrationLog);
				
				//Audit to Log
				StringBuilder builder = new StringBuilder();
				builder.append("\n");
				builder.append("################################################################################" + "\n");
				builder.append("Lumensoft : getProductsV2Response() - Start Time: " + new Date(StartTime) + "\n");
				builder.append("Time Taken: " + timeTaken + "\n");
				builder.append("URL: " + url + " [GET]\n");
				builder.append("Status: " + (errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode) + "\n");
				builder.append("Response: " + (errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc) + "\n");
				builder.append("################################################################################");
				
				log.info(builder.toString());
			}
		}
		
	}
	
	public InventoryResponse getInventoryResponse(Store store) {
		
		ResponseEntity<InventoryResponse> responseEntity = null;
		int errorCode = 0;
		String errorDesc = "";
		String url = "";
		long StartTime = System.currentTimeMillis();
		long timeTaken = 0;
		boolean eligibleForAudit = true;
		
		log.info("LumensoftRestService.getInventoryResponse() :: store=" + store.getId());
		
		try {
			url = store.getHost() + "/api/Inventory/ShopInventory?AppID=" + store.getUsername() 
				+ "&AppKey=" + store.getPassword() + "&ShopId=" + store.getVendorStoreId();
			if (store.getLastInventorySyncAt() != null) {
				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				url += "&TimeStamp=" + fmt.format(store.getLastInventorySyncAt());
			}
			
			//Get Rest Template
			RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(10);
			
			//Construct Http Entity
			HttpEntity<?> entity = new HttpEntity<>(getHeaders(store));
			
			eligibleForAudit = true;
			//Integration Call
			StartTime = System.currentTimeMillis();
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, InventoryResponse.class);
			timeTaken = (System.currentTimeMillis() - StartTime);
			
			log.info("IntegrationService.getResponse() :: status=" + responseEntity.getStatusCodeValue());
			
			return responseEntity.getBody();
		} catch(Exception e) {
			errorDesc = e.getMessage();
			errorCode = (e instanceof HttpClientErrorException) ? ((HttpClientErrorException) e).getRawStatusCode() : 999;
			throw e;
		} finally {
			
			if (eligibleForAudit) {
				
				//Audit to Database
				IntegrationLog integrationLog = new IntegrationLog();
				integrationLog.setIdentifier(store.getId());
				integrationLog.setSubIdentifier(null);
				integrationLog.setOperationType("LUMENSOFT_INBOUND_INVENTORY");
				integrationLog.setId(null);
				integrationLog.setUrl(url);
				integrationLog.setMethod("GET");
				integrationLog.setResponseStatus((errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode));
				integrationLog.setResponseTime(timeTaken);
				integrationLog.setRequest(null);
				integrationLog.setResponse((errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc).getBytes());
				integrationLog.setSystemName("LUMENSOFT");
				integrationLogService.save(integrationLog);
				
				//Audit to Log
				StringBuilder builder = new StringBuilder();
				builder.append("\n");
				builder.append("################################################################################" + "\n");
				builder.append("Lumensoft : getInventoryResponse() - Start Time: " + new Date(StartTime) + "\n");
				builder.append("Time Taken: " + timeTaken + "\n");
				builder.append("URL: " + url + " [GET]\n");
				builder.append("Status: " + (errorCode == 0 ? responseEntity.getStatusCodeValue() : errorCode) + "\n");
				builder.append("Response: " + (errorCode == 0 ? ApplicationUtil.getJsonStringFromObject(responseEntity.getBody()) : errorDesc) + "\n");
				builder.append("################################################################################");
				
				log.info(builder.toString());
			}
		}
		
	}

}
