package com.app.modal.request;

import javax.validation.constraints.NotBlank;

import com.app.annotation.FieldsValueMatch;
import com.app.annotation.ValidPassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldsValueMatch(field = "newPassword", fieldMatch = "confirmPassword", message = "new password and confirm password must be same")
//@ValidPhoneNumber(countryCodeField = "newPassword", mobileNumberField = "confirmPassword")
public class ChangePasswordRequest {
	
	@ValidPassword
	@NotBlank(message = "newPassword is mandatory")
	private String newPassword;
	
	@NotBlank(message = "confirmPassword is mandatory")
	private String confirmPassword;

}
