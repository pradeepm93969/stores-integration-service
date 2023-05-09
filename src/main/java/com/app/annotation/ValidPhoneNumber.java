package com.app.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ValidPhoneNumberConstraintValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {

    String message() default "Invalid Mobile Number";

    String countryCodeField();

    String mobileNumberField();
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
