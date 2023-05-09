package com.app.zcustom.modal.response.lumensoft;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variant {

    @JsonProperty("Product_ID")
    public Integer productID;
    @JsonProperty("ProductItemID")
    public Integer productItemID;
    @JsonProperty("product_price")
    public BigDecimal productPrice;
    @JsonProperty("ProductCode")
    public String productCode;
    @JsonProperty("Size")
    public String size;
    @JsonProperty("Color")
    public String color;

}
