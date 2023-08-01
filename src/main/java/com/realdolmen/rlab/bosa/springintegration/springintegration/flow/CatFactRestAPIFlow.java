package com.realdolmen.rlab.bosa.springintegration.springintegration.flow;

import com.realdolmen.rlab.bosa.springintegration.springintegration.model.CatFact;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.DashDashCaseCatTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.UpperCaseCatTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
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

    @Bean
    public DirectChannel inCatFactChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel outCatFactChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inCatFact() {
        log.info("Initializing inbound gateway...");
        return IntegrationFlow.from(
                        Http.inboundGateway("/catfact")
                                .requestMapping(m -> m.methods(HttpMethod.POST))
                                .mappedRequestHeaders()
                )
                .channel("inCatFactChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outCatFact() {
        return IntegrationFlow.from("inCatFactChannel")
                .handle(
                        Http.outboundGateway("https://catfact.ninja/fact") // {pathParam} appended would consider value from next step
                                //.uriVariable("pathParam", "header[customHeader]") // Fetch header value from incoming request and store in pathParam
                                .httpMethod(HttpMethod.GET)
                                .expectedResponseType(CatFact.class)
                )
                .transform(upperCaseCatTransformer)
                .transform(dashDashCaseCatTransformer)
                .get();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.consumingwebservice.wsdl");
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
                .get();
    }
}
