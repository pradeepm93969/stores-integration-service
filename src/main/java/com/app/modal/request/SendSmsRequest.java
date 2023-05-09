package com.app.modal.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsRequest {
	
	private String channel = "SMS";
	private List<String> mobileNos = new ArrayList<>();
	private String message;
	private String messageType = "TEXT";

}
