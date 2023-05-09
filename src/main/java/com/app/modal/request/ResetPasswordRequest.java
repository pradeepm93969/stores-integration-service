package com.app.modal.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordRequest {
	
	@Email
	private String email;
	
	@Min(1)
	@Max(10000)
	private int mobileNumberCountryCode;
	
	private String mobileNumber;

}
