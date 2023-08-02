package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.flow;

import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.aggregator.DemoAggregatingMessageGroupProcessor;
import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model.CatFact;
import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model.Test;
import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.transformer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.ExpressionEvaluatingReleaseStrategy;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.integration.scattergather.ScatterGatherHandler;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.ws.dsl.Ws;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class CatFactRestAPIFlow {

    @Autowired
    public MessageChannel getCountryAsStringChannel;
    @Autowired
    public MessageChannel getCountryUpperCaseChannel;
    @Autowired
    public MessageChannel getCatFactChannel;
    @Autowired
    public MessageChannel outputChannelAggregator;
    @Autowired
    UpperCaseCatTransformer upperCaseCatTransformer;
    @Autowired
    DashDashCaseCatTransformer dashDashCaseCatTransformer;
    @Autowired
    GetCountryRequestTransformer getCountryRequestTransformer;
    @Autowired
    GetCountryResponseTransformer getCountryResponseTransformer;
    @Autowired
    UpperCaseCountryTransformer upperCaseCountryTransformer;
    @Autowired
    LowerCaseCountryTransformer lowerCaseCountryTransformer;
    @Autowired
    CountryToStringTransformer countryToStringTransformer;

    @Bean
    public IntegrationFlow inCatFact() {
        log.info("GATEWAY-INBOUND__________________________________ Initializing");
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
                .log(message -> "CHANNEL  __________________________________ inCatFactChannel")
                .handle(
                        Ws.marshallingOutboundGateway()

                                .uri("http://localhost:8080/ws/CountriesPortSoap11/getCountry")
                                .marshaller(marshaller)
                                .unmarshaller(marshaller)
                )
                .channel("outChannel")
                .get();
    }

    @Bean("scatterGatherDistributor")
    protected MessageHandler scatterGatherDistributor() {
        RecipientListRouter router = new RecipientListRouter();
        router.setApplySequence(true);
        router.setChannels(Arrays.asList(getCountryAsStringChannel, getCountryUpperCaseChannel, getCatFactChannel));
        return router;
    }

    @Bean("scatterGatherGatherer")
    public MessageHandler scatterGatherGatherer() {
        return new AggregatingMessageHandler(
                new DemoAggregatingMessageGroupProcessor(),
                new SimpleMessageStore(),
                new HeaderAttributeCorrelationStrategy(
                        IntegrationMessageHeaderAccessor.CORRELATION_ID),
                new ExpressionEvaluatingReleaseStrategy("size() == 3"));
    }

    @Bean("scatterGatherDistribution")
    protected MessageHandler scatterGatherDistribution(@Qualifier("scatterGatherDistributor") MessageHandler distributor, @Qualifier("scatterGatherGatherer") MessageHandler gatherer) {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor, gatherer);
        handler.setOutputChannel(outputChannelAggregator);
        return handler;
    }

    @Bean
    public IntegrationFlow orders(@Qualifier("scatterGatherDistribution") MessageHandler scatterGatherDistribution) {
        return IntegrationFlow
                .from("outChannel")
                .handle(scatterGatherDistribution)
                .get();
    }

    @Bean
    public IntegrationFlow outSubChannel1() {
        return IntegrationFlow.from("getCountryAsStringChannel")
                .log(message -> "CHANNEL  __________________________________ getCountryAsStringChannel")
                .transform(countryToStringTransformer)
                .get();
    }

    @Bean
    public IntegrationFlow outSubChannel2() {
        return IntegrationFlow.from("getCountryUpperCaseChannel")
                .log(message -> "CHANNEL  __________________________________ getCountryUpperCaseChannel")
                .transform(upperCaseCountryTransformer)
                .get();
    }

    @Bean
    public IntegrationFlow outGetCatFactChannel() {
        return IntegrationFlow.from("getCatFactChannel")
                .log(message -> "CHANNEL  __________________________________ getCatFactChannel")
                .handle(
                        Http.outboundGateway("https://catfact.ninja/fact") // {pathParam} appended would consider value from next step
                                //.uriVariable("pathParam", "header[customHeader]") // Fetch header value from incoming request and store in pathParam
                                .httpMethod(HttpMethod.GET)
                                .expectedResponseType(CatFact.class)
                )
                .enrichHeaders(h -> h.header("Content-type", "application/xml"))
                .transform(upperCaseCatTransformer)
                .transform(dashDashCaseCatTransformer)
                .get();
    }

    @Bean
    public IntegrationFlow outputFinal() {
        return IntegrationFlow.from("outputChannelAggregator")
                .log(message -> "CHANNEL  __________________________________ outputChannelAggregator")
                .get();
    }


    @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
    public Message<?> processAsyncScatterError(MessagingException payload) {
        return MessageBuilder.withPayload(payload.getCause().getCause())
                .copyHeaders(payload.getFailedMessage().getHeaders())
                .build();
    }
}
