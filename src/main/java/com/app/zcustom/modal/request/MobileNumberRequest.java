package com.app.zcustom.modal.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.app.annotation.ValidPhoneNumber;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ValidPhoneNumber(countryCodeField = "mobileNumberCountryCode", mobileNumberField = "mobileNumber")
public class MobileNumberRequest {
	
    private String mobileNumber;
    
    @Min(1)
	@Max(10000)
    private int mobileNumberCountryCode;
    

}
