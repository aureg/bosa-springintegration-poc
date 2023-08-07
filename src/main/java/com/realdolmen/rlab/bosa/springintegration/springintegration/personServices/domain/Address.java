package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Accessors(chain = true)
@Data
public class Address {
    protected String street;
    protected String city;
    protected BigInteger postalcode;
    protected String number;
    protected String country;
}
