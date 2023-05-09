package com.app.zcustom.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.repository.entity.ApiConfiguration;
import com.app.repository.entity.IntegrationLog;
import com.app.service.ApiConfigurationService;
import com.app.service.IntegrationService;
import com.app.zcustom.modal.request.magento.MagentoLoginRequest;
import com.app.zcustom.modal.request.magento.MagentoUpdatePriceInventoryRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MagentoRestService extends IntegrationService {
	
	public static final String MAGENTO_LOGIN = "MAGENTO_LOGIN";
	public static final String MAGENTO_VENDOR_PRODUCT_UPDATE = "MAGENTO_VENDOR_PRODUCT_UPDATE";
	
	@Autowired
	private ApiConfigurationService apiConfigurationService;
	
	public String getToken() {
		log.info("Get Magento Token");
		ResponseEntity<String> response = null;
		
		ApiConfiguration configuration = apiConfigurationService.getById(MAGENTO_LOGIN);
		MagentoLoginRequest request = new MagentoLoginRequest();
		request.setUsername(configuration.getUsername());
		request.setPassword(configuration.getPassword());
		
		IntegrationLog integrationLog = new IntegrationLog();
		integrationLog.setIdentifier(configuration.getUsername());
		integrationLog.setSubIdentifier(null);
		integrationLog.setOperationType("GET_TOKEN");
		
		try {
			response = getResponse(MAGENTO_LOGIN, getHeaders(MAGENTO_LOGIN), null, request, String.class, integrationLog);
		} catch(Exception e) {
			throw new ResponseStatusException(
					response == null ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.resolve(response.getStatusCodeValue()),
							e.getLocalizedMessage());
		}
		return response.getBody().replaceAll("^\"|\"$", "");
	}
	
	public String updatePriceAndInventory(MagentoUpdatePriceInventoryRequest request, String token) {
		
		ResponseEntity<String> response = null;
		
		IntegrationLog integrationLog = new IntegrationLog();
		integrationLog.setOperationType("UPDATE");
		
		HttpHeaders headers = getHeaders(MAGENTO_VENDOR_PRODUCT_UPDATE);
		headers.add("Authorization", "Bearer " + token);
		
		try {
			response = getResponse(MAGENTO_VENDOR_PRODUCT_UPDATE, headers, null, request, String.class, integrationLog);
		} catch(Exception e) {
			throw new ResponseStatusException(
					response == null ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.resolve(response.getStatusCodeValue()),
							e.getLocalizedMessage());
		}
		return response.getBody();
		
	}

}
