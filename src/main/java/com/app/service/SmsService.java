package com.app.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.modal.request.SendSmsRequest;
import com.app.repository.SmsScheduledRepository;
import com.app.repository.entity.IntegrationLog;
import com.app.repository.entity.SmsConfiguration;
import com.app.repository.entity.SmsScheduled;
import com.app.service.integration.SmsGatewayWebService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsService {
	
	@Autowired
	private SmsConfigurationService smsConfigurationService;
	
	@Autowired
	private SmsGatewayWebService smsGatewayWebService;
	
	@Autowired
	private SmsScheduledRepository smsScheduledRepository;
	
	public void sendTemplateSms(String smsType, int countryCode, long mobileNumber, 
			Map<String, String> messageMap, String identifier, String subIdentifier, String operationType) {
		
		SmsConfiguration smsConfiguration = smsConfigurationService.getById(smsType);
		Thread thread = new Thread(() -> {
			if (smsConfiguration.isEnabled()) {
				String message = smsConfiguration.getMessage();
				
				if (messageMap != null) {
					StringSubstitutor sub = new StringSubstitutor(messageMap);
					message = sub.replace(message);
				}
				if (!smsConfiguration.isWhitelisted() || (
						smsConfiguration.isWhitelisted() && Arrays.asList(smsConfiguration.getWhitelistedNumbers().split(",")).contains(
								Integer.toString(countryCode) + Long.toString(mobileNumber)))) {
					this.sendSms(countryCode, Long.toString(mobileNumber), message, identifier, subIdentifier, operationType);
				} else {
					log.debug("Mobile Number " + countryCode + mobileNumber + 
							" is not whitelisted for Sms type " + smsType);
				}
			}
		});
		thread.start();
	}
	
	public void sendScheduledTemplateSms(String smsType, int countryCode, String mobileNumber, 
			Map<String, String> messageMap, String identifier, String subIdentifier, String operationType) {
		
		SmsConfiguration smsConfiguration = smsConfigurationService.getById(smsType);
		if (smsConfiguration.isEnabled()) {
			String message = smsConfiguration.getMessage();
			
			if (messageMap != null) {
				StringSubstitutor sub = new StringSubstitutor(messageMap);
				message = sub.replace(message);
			}
			if (!smsConfiguration.isWhitelisted() || (
					smsConfiguration.isWhitelisted() && Arrays.asList(smsConfiguration.getWhitelistedNumbers().split(",")).contains(
							Integer.toString(countryCode) + mobileNumber))) {
				
				SmsScheduled smsScheduled = new SmsScheduled();
				smsScheduled.setMobileNumberCountryCode(countryCode);
				smsScheduled.setMobileNumber(mobileNumber);
				smsScheduled.setSmsMessage(message);
				smsScheduled.setProcessed(false);
				smsScheduled.setRetryCount(0);
				smsScheduled.setNextRunAt(OffsetDateTime.now());
				smsScheduledRepository.save(smsScheduled);
			} else {
				log.debug("Mobile Number " + countryCode + mobileNumber + 
						" is not whitelisted for Sms type " + smsType);
			}
		}
	}

	public void sendSms(int countryCode, String mobileNumber, String message, String identifier, 
			String subIdentifier, String operationType) {
		
		SendSmsRequest request = new SendSmsRequest();
		request.setMessage(message);
		request.getMobileNos().add("00" + Integer.toString(countryCode) + mobileNumber);
		
		IntegrationLog integrationLog = new IntegrationLog();
		integrationLog.setIdentifier(identifier);
		integrationLog.setSubIdentifier(subIdentifier);
		integrationLog.setOperationType(operationType);
		smsGatewayWebService.sendSms(request, integrationLog);
	}
	
}
