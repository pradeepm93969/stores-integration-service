package com.app.zcustom.modal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MobileNumberResponse {
	
    private String mobileNumber;
    private int mobileNumberCountryCode;
    private String messageId;

}
