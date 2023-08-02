package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.Country;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetCountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LowerCaseCountryTransformer {
    @Transformer
    public Country lowerCaseTransformer(GetCountryResponse payload) {
        log.error("TRANSFORM__________________________________ LowerCaseCountryTransformer");

        Country country = new Country();
        country.setName(payload.getCountry().getName().toLowerCase());
        country.setCapital(payload.getCountry().getCapital().toLowerCase());
        country.setCurrency(payload.getCountry().getCurrency());
        country.setPopulation(payload.getCountry().getPopulation());

        return country;
    }
}
