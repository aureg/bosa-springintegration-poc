package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;

@Configuration
public class PersonChannelConfiguration {

    @Bean
    public DirectChannel inboundGatewayPersonRestChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel outBoundNrSoapRequestChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel errorRouting() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel getSsrPersonRequestChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel errorChannel() {
        return new DirectChannel();
    }
}
