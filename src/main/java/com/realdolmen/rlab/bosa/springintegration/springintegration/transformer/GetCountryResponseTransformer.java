package com.realdolmen.rlab.bosa.springintegration.springintegration.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.Country;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetCountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetCountryResponseTransformer {
    @Transformer
    public Country transformGetCountry(Object payload) {
        log.error("TRANSFORM__________________________________GetCountryResponseTransformer");
        if (payload instanceof GetCountryResponse) {
            log.error("I'm a get country response");
            return ((GetCountryResponse) payload).getCountry();
        } else {
            log.error("I'm NOT a get country response");
        }
        return new Country();
    }
}
