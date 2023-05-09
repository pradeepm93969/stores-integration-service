package com.app.modal.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticationRequest {
	
	@NotBlank(message = "grantType is mandatory")
	@Pattern(regexp="password|refresh_token|client_credentials", message="grantType must be either password|refresh_token|client_credentials")
	private String grantType;
	
	@Email
	private String email;
	
	@Min(1)
	@Max(10000)
	private int mobileNumberCountryCode;
	
	private String mobileNumber;

	private String password;

}
