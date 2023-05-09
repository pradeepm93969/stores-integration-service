package com.app.zcustom.modal.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendNotificationRequest {
	
	@NotEmpty
	@NotEmpty(message = "mobileNos is mandatory")
	private List<MobileNumberRequest> mobileNos;
	
	@NotNull(message = "message is mandatory")
	private String message;
	
	@NotNull(message = "messageType is mandatory")
	@Pattern(regexp = "TEXT")
	private String messageType;

}
