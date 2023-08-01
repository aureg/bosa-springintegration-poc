package com.realdolmen.rlab.bosa.springintegration.springintegration.flow;

import com.realdolmen.rlab.bosa.springintegration.springintegration.model.Test;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.DashDashCaseCatTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.GetCountryRequestTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.GetCountryResponseTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.UpperCaseCatTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ws.dsl.Ws;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CatFactRestAPIFlow {

    @Autowired
    UpperCaseCatTransformer upperCaseCatTransformer;
    @Autowired
    DashDashCaseCatTransformer dashDashCaseCatTransformer;
    @Autowired
    GetCountryRequestTransformer getCountryRequestTransformer;
    @Autowired
    GetCountryResponseTransformer getCountryResponseTransformer;


    @Bean
    public IntegrationFlow inCatFact() {
        log.info("Initializing inbound gateway...");
        return IntegrationFlow.from(
                        Http.inboundGateway("/catfact")
                                .requestMapping(m -> m.methods(HttpMethod.POST))
                                .mappedRequestHeaders("countryinheader")
                                .requestPayloadType(Test.class)
                )
                .transform(getCountryRequestTransformer, "transformGetCountry")
                .channel("inCatFactChannel")
                .get();
    }


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl");
        return marshaller;
    }


    @Bean
    IntegrationFlow outboundCountry(Jaxb2Marshaller marshaller) {
        return IntegrationFlow.from("inCatFactChannel")
                .log(message -> "before calling ws country")
                .handle(
                        Ws.marshallingOutboundGateway()

                                .uri("http://localhost:8080/ws/CountriesPortSoap11/getCountry")
                                .marshaller(marshaller)
                                .unmarshaller(marshaller)
                )
                .log(message -> "after calling ws country")
                .channel("outChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outCatGate() {
        return IntegrationFlow.from("outChannel")
                .log(message -> "start outGate")
                .transform(getCountryResponseTransformer)
                .log(message -> "before outGate get")
                .get();
    }
}
