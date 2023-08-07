package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.domain;

import lombok.Data;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Data
public class Person {
    String firstname;
    String lastname;
    Long niss;
    String email;
    Address address;
}
