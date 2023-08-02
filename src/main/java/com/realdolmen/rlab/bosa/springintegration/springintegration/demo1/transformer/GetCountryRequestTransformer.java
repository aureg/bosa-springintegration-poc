package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model.Test;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetCountryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetCountryRequestTransformer {
    @Transformer
    public GetCountryRequest transformGetCountry(Test payload, @Header("countryinheader") String countryInHeader) {
        log.error("TRANSFORM__________________________________ GetCountryRequestTransformer: " + countryInHeader);
        GetCountryRequest getCountryRequest = new GetCountryRequest();
        getCountryRequest.setName(countryInHeader);
        return getCountryRequest;
    }
}
