package com.app.zcustom.modal.response.lumensoft;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryData {

	@JsonProperty("Hold_Quantity")
	public Float holdQuantity;
	@JsonProperty("Product_code")
	public String productCode;
	@JsonProperty("quantity")
	public Float quantity;

}
