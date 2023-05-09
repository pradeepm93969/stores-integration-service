
package com.app.zcustom.modal.request.magento;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyCatalogItem {

    @JsonProperty("company_id")
    public Integer companyId;
    @JsonProperty("sku")
    public String sku;
    @JsonProperty("quantity")
    public Integer quantity;
    @JsonProperty("status")
    public Integer status;
    @JsonProperty("price")
    public BigDecimal price;
    @JsonProperty("special_price")
    public BigDecimal specialPrice;

}
