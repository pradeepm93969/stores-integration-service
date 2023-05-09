package com.app.modal.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenResponse {
	
	private Long userId;
	private String tokenType;
	private String firstName;
	private String avatarImage;
	private List<String> roles;

}
