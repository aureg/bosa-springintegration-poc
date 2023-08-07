package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices;

import com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.transformer.NrPersonTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.transformer.SsrPersonTransformer;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetNrPersonRequest;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetSsrPersonRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ws.dsl.Ws;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonServicesRestAPI {

    @Autowired
    public MessageChannel inboundGatewayPersonRestChannel;
    @Autowired
    public MessageChannel outBoundNrSoapRequestChannel;
    @Autowired
    public MessageChannel getSsrPersonRequestChannel;
    @Autowired
    public DirectChannel errorChannel;
    @Autowired
    public DirectChannel errorRouting;
    @Autowired
    NrPersonTransformer nrPersonTransformer;
    @Autowired
    SsrPersonTransformer ssrPersonTransformer;

    @Bean
    public IntegrationFlow inboundGatewayPersonRest() {
        log.info("GATEWAY-INBOUND__________________________________ Initializing PersonRest");
        return IntegrationFlow.from(
                        Http.inboundGateway("/rest/person")
                                .requestMapping(m -> m.methods(HttpMethod.GET))
                                .mappedRequestHeaders("countryinheader")
                                .payloadExpression(new String("#requestParams['niss'][0]?:''"))
                                .errorChannel(errorChannel)
                )
                .channel(inboundGatewayPersonRestChannel)
                .get();
    }


    @Bean
    public IntegrationFlow GetNrPersonFromSOAP(Jaxb2Marshaller marshallerPerson) {
        return IntegrationFlow.from(inboundGatewayPersonRestChannel)
                .log(message -> "OUT________________________________________ GetNrPersonFromSOAP")
                .transform(nrPersonTransformer, "toGetNrPersonRequest")
                .handle(
                        Ws.marshallingOutboundGateway()
                                .uri("http://localhost:8080/ws/NrPersonServicePortSoap11/getNrPerson")
                                .marshaller(marshallerPerson)
                                .unmarshaller(marshallerPerson)
                )
                .transform(nrPersonTransformer, "toPerson")
                .channel(outBoundNrSoapRequestChannel)
                .get();
    }

    @Bean
    public IntegrationFlow GetSsrPersonFromSOAP(Jaxb2Marshaller marshallerPerson) {
        return IntegrationFlow.from(getSsrPersonRequestChannel)
                .log(message -> "OUT________________________________________ GetSsrPersonFromSOAP")
                .transform(ssrPersonTransformer, "toGetSsrPersonRequest")
                .handle(
                        Ws.marshallingOutboundGateway()
                                .uri("http://localhost:8080/ws/SsrPersonServicePortSoap11/getSsrPerson")
                                .marshaller(marshallerPerson)
                                .unmarshaller(marshallerPerson)
                )
                .transform(ssrPersonTransformer, "toPerson")
                .channel(outBoundNrSoapRequestChannel)
                .get();
    }

    @Bean
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlow
                .from(errorChannel)
                .handle(new GenericHandler() {
                    @Override
                    public Object handle(Object payload, MessageHeaders headers) {

                        log.error("Handle errors: errorHandlingFlow");
                        if (payload instanceof MessagingException) {

                            MessagingException message = (MessagingException) payload;

                            if (message.getFailedMessage().getPayload() instanceof GetNrPersonRequest) {
                                GetNrPersonRequest getNrPersonRequest = (GetNrPersonRequest) message.getFailedMessage().getPayload();
                                log.error("failed GetNrPersonRequest, will try GetSsrPersonRequest :");

                                GetSsrPersonRequest getSsrPersonRequest = new GetSsrPersonRequest();
                                getSsrPersonRequest.setNiss(getNrPersonRequest.getNiss());
                                return getSsrPersonRequest;
                            }
                            if (message.getFailedMessage().getPayload() instanceof GetSsrPersonRequest) {
                                log.error("failed GetSsrPersonRequest, return not found");
                                GetSsrPersonRequest getSsrPersonRequest = (GetSsrPersonRequest) message.getFailedMessage().getPayload();
                                return (new NotFoundException().setCause("People not found with niss " + getSsrPersonRequest.getNiss()));
                            }
                        }
                        return "error";
                    }
                })
                .channel(errorRouting)
                .get();
    }

    @Bean
    public IntegrationFlow errorRoutingFlow() {
        return IntegrationFlow
                .from(errorRouting)
                .transform(source -> {
                    if (source instanceof MessagingException) {
                        MessagingException messagingException = ((MessagingException) source);
                        return messagingException.getFailedMessage();
                    }
                    return source;
                })
                .<Object, Class<?>>route(Object::getClass, m -> m
                        .channelMapping(GetSsrPersonRequest.class, getSsrPersonRequestChannel)
                        .channelMapping(String.class, outBoundNrSoapRequestChannel)
                        .channelMapping(NotFoundException.class, outBoundNrSoapRequestChannel)
                )
                .get();
    }


    @Bean
    public IntegrationFlow outputPersonRest() {
        return IntegrationFlow.from(outBoundNrSoapRequestChannel)
                .transform(source -> {
                    return source;
                }) // do nothing but needed to make this flow exists
                .log(message -> "OUT________________________________________ outputPersonRest")
                .get();
    }

    @Bean
    public Jaxb2Marshaller marshallerPerson() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl");
        return marshaller;
    }


}
