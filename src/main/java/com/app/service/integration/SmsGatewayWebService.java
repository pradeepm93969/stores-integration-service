package com.app.service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import com.app.modal.request.SendSmsRequest;
import com.app.modal.response.SendSmsResponse;
import com.app.repository.entity.IntegrationLog;
import com.app.service.EmailService;
import com.app.service.IntegrationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SmsGatewayWebService extends IntegrationService {
	
	@Autowired
	private EmailService emailService;

	private static boolean alertEmailSent = false;

	public void sendSms(SendSmsRequest request, IntegrationLog integrationLog) {
		log.info("send Sms Started from SMS Gateway");
		
		String apiConfigurationName = "CNS_SMS";
		ResponseEntity<SendSmsResponse> response = null;
		
		try {
			response = getResponse(apiConfigurationName, getHeaders(apiConfigurationName), null, request, SendSmsResponse.class, integrationLog);
			
			sendRecoveryMail("SMS Gateway has recovered", "SMS Gateway has recovered", null);
		} catch(Exception e) {
			if (e instanceof ResourceAccessException) {
				sendAlertMail("SMS Gateway is down", "Kindly check the SMS gateway webservice", e);
			}
			throw new ResponseStatusException(
					response == null ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.resolve(response.getStatusCodeValue()),
							e.getLocalizedMessage());
		}
	}

	private void sendAlertMail(String subject, String message, Exception e) {
		if (!SmsGatewayWebService.alertEmailSent) {
			log.error(message);
			try {
				this.emailService.sendAlertEmail(subject, message, e);
				SmsGatewayWebService.alertEmailSent = true;
			} catch (Exception ex) {
				log.error(message + "email notification failed");
			}
		}
	}
	
	private void sendRecoveryMail(String subject, String message, Exception e) {
		if (SmsGatewayWebService.alertEmailSent) {
			log.error(message);
			try {
				this.emailService.sendAlertEmail(subject, message, e);
				SmsGatewayWebService.alertEmailSent = false;
			} catch (Exception ex) {
				log.error(message + "email notification failed");
			}
		}
	}
}
