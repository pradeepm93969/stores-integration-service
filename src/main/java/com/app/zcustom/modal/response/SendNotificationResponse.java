package com.app.zcustom.modal.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponse {
	
	private List<MobileNumberResponse> response;

}
