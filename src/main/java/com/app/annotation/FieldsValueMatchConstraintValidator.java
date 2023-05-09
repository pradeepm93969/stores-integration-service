package com.app.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class FieldsValueMatchConstraintValidator implements ConstraintValidator<FieldsValueMatch, Object> {

	private String field;
	private String fieldMatch;

	@Override
	public void initialize(FieldsValueMatch constraintAnnotation) {
		this.field = constraintAnnotation.field();
		this.fieldMatch = constraintAnnotation.fieldMatch();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		try {
			Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
			Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);
	
			if (fieldValue != null) {
				return fieldValue.equals(fieldMatchValue);
			} else {
				return fieldMatchValue == null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
