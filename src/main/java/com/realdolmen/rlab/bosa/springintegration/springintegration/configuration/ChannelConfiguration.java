package com.realdolmen.rlab.bosa.springintegration.springintegration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;

@Configuration
public class ChannelConfiguration {

    @Bean
    public DirectChannel sampleChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel nextServiceChannel() {
        return new DirectChannel();
    }
}
