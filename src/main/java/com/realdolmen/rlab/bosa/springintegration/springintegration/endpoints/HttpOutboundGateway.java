package com.realdolmen.rlab.bosa.springintegration.springintegration.endpoints;


import com.realdolmen.rlab.bosa.springintegration.springintegration.model.CatFact;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.DashDashCaseCatTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.UpperCaseCatTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.stereotype.Component;


@Component
public class HttpOutboundGateway {

    @Autowired
    UpperCaseCatTransformer upperCaseCatTransformer;
    @Autowired
    DashDashCaseCatTransformer dashDashCaseCatTransformer;

    @Bean
    public IntegrationFlow outGate() {
        return IntegrationFlow.from("sampleChannel")
                .log(message -> "start outGate")
                .handle(
                        Http.outboundGateway("https://catfact.ninja/fact") // {pathParam} appended would consider value from next step
                                //.uriVariable("pathParam", "header[customHeader]") // Fetch header value from incoming request and store in pathParam
                                .httpMethod(HttpMethod.GET)
                                .expectedResponseType(CatFact.class)
                )
                .enrichHeaders(h -> h.header("Content-type", "application/xml"))
                .transform(upperCaseCatTransformer)
                .transform(dashDashCaseCatTransformer)
                .log(message -> "before outGate get")
                .get();
    }
}
