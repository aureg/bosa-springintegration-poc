package com.realdolmen.rlab.bosa.springintegration.springintegration.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpInboundGateway {

    @Bean
    public IntegrationFlow inGate() {
        log.info("Initializing inbound gateway...");
        return IntegrationFlow.from(
                        Http.inboundGateway("/checkInbound")
                                .requestMapping(m -> m.methods(HttpMethod.POST))
                                .mappedRequestHeaders("customHeader")
                                .mappedResponseHeaders("testheader", "HTTP_RESPONSE_HEADERS")
                                .id("idInGate")
                )
                .log(message -> "hello")
                //.headerFilter("accept-encoding", false)
                .channel("sampleChannel")
                .get();
    }


    @Bean
    public IntegrationFlow inGateGet() {
        log.info("Initializing inbound gateway get...");
        return IntegrationFlow.from(
                        Http.inboundGateway("/checkInbound").requestMapping(m -> m.methods(HttpMethod.GET))
                )
                .enrichHeaders(h -> h.header("testttt", "inboundHeader"))
                .headerFilter("accept-encoding", false)
                .channel("sampleChannel")
                .get();
    }
}
