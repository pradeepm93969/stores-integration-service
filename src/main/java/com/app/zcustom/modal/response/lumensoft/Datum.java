package com.app.zcustom.modal.response.lumensoft;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Datum {

    public String itemName;
    public String webItem;
    public List<Variant> variants = null;

}
