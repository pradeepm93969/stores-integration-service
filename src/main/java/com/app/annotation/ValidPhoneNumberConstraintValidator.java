package com.app.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class ValidPhoneNumberConstraintValidator implements ConstraintValidator<ValidPhoneNumber, Object> {

	private String countryCode;
	private String mobileNumber;

	@Override
	public void initialize(ValidPhoneNumber constraintAnnotation) {
		this.countryCode = constraintAnnotation.countryCodeField();
		this.mobileNumber = constraintAnnotation.mobileNumberField();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		try {
			Object countryCodeValue = new BeanWrapperImpl(value).getPropertyValue(countryCode);
			Object mobileNumberValue = new BeanWrapperImpl(value).getPropertyValue(mobileNumber);
	
			if ((int) countryCodeValue > 0 && Long.parseLong((String) mobileNumberValue) > 0) {
				PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
				PhoneNumber number = new PhoneNumber();
			    number.setCountryCode((int) countryCodeValue)
	    			.setNationalNumber(Long.parseLong((String) mobileNumberValue));
			    return phoneNumberUtil.isValidNumber(number);
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
