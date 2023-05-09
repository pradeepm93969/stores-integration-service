package com.app.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@Table(name = "SMS_SCHEDULED")
@SequenceGenerator(initialValue = 100, allocationSize = 1, name = "idgen", sequenceName = "SMS_SCHEDULED_SEQUENCE")
public class SmsScheduled extends BaseScheduledEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	private Long id;

	@Column(name = "MOBILE_NUMBER", length = 15)
    private String mobileNumber;
    
    @Column(name = "MOBILE_NUMBER_COUNTRY_CODE")
    private int mobileNumberCountryCode;
    
	@Column(name = "SMS_MESSAGE", nullable = false, length = 1000)
    private String smsMessage;
	
}
