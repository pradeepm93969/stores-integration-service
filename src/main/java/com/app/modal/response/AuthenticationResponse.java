package com.app.modal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticationResponse {

	private String accessToken;
	private String tokenType = "bearer";
	private String refreshToken;
}
