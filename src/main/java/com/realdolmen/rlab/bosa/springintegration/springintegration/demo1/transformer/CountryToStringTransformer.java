package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetCountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CountryToStringTransformer {
    @Transformer
    public String toStringTransformer(GetCountryResponse payload) {
        log.error("TRANSFORM__________________________________ CountryToStringTransformer");

        String country = "Country: "
                + payload.getCountry().getName()
                + " -- capital: " + payload.getCountry().getCapital()
                + " -- population: " + payload.getCountry().getPopulation()
                + " -- currency: " + payload.getCountry().getCurrency().value();

        return country;
    }
}
