package com.app.zcustom.modal.response.lumensoft;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    public String code;
    public String msg;
    public List<InventoryData> data = null;

}
