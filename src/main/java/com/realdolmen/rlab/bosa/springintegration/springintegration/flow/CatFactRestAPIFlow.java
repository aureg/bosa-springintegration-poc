package com.realdolmen.rlab.bosa.springintegration.springintegration.flow;

import com.realdolmen.rlab.bosa.springintegration.springintegration.aggregator.DemoAggregatingMessageGroupProcessor;
import com.realdolmen.rlab.bosa.springintegration.springintegration.model.Test;
import com.realdolmen.rlab.bosa.springintegration.springintegration.transformer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.ExpressionEvaluatingMessageGroupProcessor;
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
    @Autowired
    public MessageChannel subChannel1;
    @Autowired
    public MessageChannel subChannel2;
    @Autowired
    public MessageChannel outputChannelAggregator;


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

    private MessageHandler distributor() {
        RecipientListRouter router = new RecipientListRouter();
        router.setApplySequence(true);
        router.setChannels(Arrays.asList(subChannel1, subChannel2));
        return router;
    }
    public MessageHandler gatherer() {
        return new AggregatingMessageHandler(
                new DemoAggregatingMessageGroupProcessor(),
                new SimpleMessageStore(),
                new HeaderAttributeCorrelationStrategy(
                        IntegrationMessageHeaderAccessor.CORRELATION_ID),
                new ExpressionEvaluatingReleaseStrategy("size() == 2"));
    }
    private MessageHandler scatterGatherDistribution() {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor(), gatherer());
        handler.setOutputChannel(outputChannelAggregator);
        return handler;
    }


    @Bean
    public IntegrationFlow orders() {
        return IntegrationFlow
                .from("outChannel")
                .handle(scatterGatherDistribution())
                .get();
    }

    @Bean
    public IntegrationFlow outSubChannel1() {
        return IntegrationFlow.from("subChannel1")
                .log(message -> "start subChannel1")
                .transform(countryToStringTransformer)
                .get();
    }
    @Bean
    public IntegrationFlow outSubChannel2() {
        return IntegrationFlow.from("subChannel2")
                .log(message -> "start subChannel2")
                .transform(upperCaseCountryTransformer)
                .get();
    }
    @Bean
    public IntegrationFlow outputFinal() {
        return IntegrationFlow.from("outputChannelAggregator")
                .log(message -> "start outputChannelAggregator")
                .get();
    }


    @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
    public Message<?> processAsyncScatterError(MessagingException payload) {
        return MessageBuilder.withPayload(payload.getCause().getCause())
                .copyHeaders(payload.getFailedMessage().getHeaders())
                .build();
    }
}
