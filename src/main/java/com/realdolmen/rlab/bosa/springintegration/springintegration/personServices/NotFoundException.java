package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotFoundException {

    String cause;
}
