package com.app.zcustom.modal.request.magento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MagentoLoginRequest {
	private String username;
	private String password;
}
