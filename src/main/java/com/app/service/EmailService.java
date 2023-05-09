package com.app.service;

import java.util.Date;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.repository.entity.EmailConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailConfigurationService emailConfigurationService;
	
	@Autowired
	private CommonConfigurationService commonConfigurationService;
	
	public void sendTemplateMail(String mailType, String ToList, String ccList, String bccList, Map<String, String> subjectMap, 
			Map<String, String> bodyMap) {
		
		EmailConfiguration emailConfiguration = emailConfigurationService.getById(mailType);
		Thread thread = new Thread(() -> {
			if (emailConfiguration.isEnabled()) {
				String subject = emailConfiguration.getSubject();
				String body = emailConfiguration.getBody();
				
				if (subjectMap != null) {
					StringSubstitutor sub = new StringSubstitutor(subjectMap);
					subject = sub.replace(subject);
				}
				
				if (bodyMap != null) {
					StringSubstitutor sub = new StringSubstitutor(bodyMap);
					body = sub.replace(body);
				}
				
				this.sendEmail(ToList, ccList, bccList, subject, body);
			}
		});
		thread.start();
	}
	
	public void sendAlertEmail(String subject, String body, Exception e) {
		sendEmail(commonConfigurationService.getById("ALERT_EMAILS").getValue(), null, null, subject, 
				"Hi\n\n" + body + "\n\n" + (e != null ? ExceptionUtils.getStackTrace(e) : null));
	}
	
	public void sendAlertEmail(String alertEmails, String subject, String body, Exception e) {
		sendEmail(alertEmails, null, null, subject, 
				"Hi\n\n" + body + "\n\n" + (e != null ? ExceptionUtils.getStackTrace(e) : null));
	}
	
	public void sendEmail(String ToList, String ccList, String bccList, String subject, String body) {
	    try {
		   MimeMessage message = javaMailSender.createMimeMessage();
		   
		   MimeMessageHelper messageHelper = new MimeMessageHelper(message, "utf-8");
		   messageHelper.setFrom(new InternetAddress(
				   commonConfigurationService.getById("EMAIL_FROM_ADDRESS").getValue(), false));
		   messageHelper.setTo(InternetAddress.parse(ToList));
		   if (StringUtils.isNotBlank(ccList))  messageHelper.setCc(InternetAddress.parse(ccList));
		   if (StringUtils.isNotBlank(bccList))  messageHelper.setBcc(InternetAddress.parse(bccList));
		   messageHelper.setSubject(subject);
		   messageHelper.setText(body, true);
		   messageHelper.setSentDate(new Date());
	
		   javaMailSender.send(message);  
		   log.info(subject + " email successfully sent to " + ToList);
	   } catch (Exception exp) {
		   log.error("Error while sending email" + exp.getMessage());
	   }
	   
	}

}