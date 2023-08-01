package com.realdolmen.rlab.bosa.springintegration.springintegration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

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


    @Bean
    public DirectChannel inCatFactChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel outCatFactChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel outChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel getCountryAsStringChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel getCountryUpperCaseChannel() {
        return new DirectChannel();

    }

    @Bean
    public MessageChannel getCatFactChannel() {
        return new DirectChannel();

    }

    @Bean
    public MessageChannel outputChannelAggregator() {
        return new DirectChannel();
    }
}
