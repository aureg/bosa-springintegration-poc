package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model;

import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.Country;
import lombok.Data;

@Data
public class CountryAggregated {
    private Country country;
    private String extraString;
    private String catFactAsString;
}
