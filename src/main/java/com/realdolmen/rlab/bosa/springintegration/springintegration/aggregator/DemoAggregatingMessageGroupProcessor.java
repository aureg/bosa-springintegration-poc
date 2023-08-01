package com.realdolmen.rlab.bosa.springintegration.springintegration.aggregator;

import com.realdolmen.rlab.bosa.springintegration.springintegration.model.CountryAggregated;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.Country;
import org.springframework.integration.aggregator.AbstractAggregatingMessageGroupProcessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import java.util.Map;

public class DemoAggregatingMessageGroupProcessor extends AbstractAggregatingMessageGroupProcessor {
    @Override
    protected CountryAggregated aggregatePayloads(MessageGroup group, Map<String, Object> defaultHeaders) {

        CountryAggregated countryAggregated = new CountryAggregated();

        for(Message message : group.getMessages() ){
            if(message.getPayload() instanceof String){
                countryAggregated.setExtraString((String)message.getPayload());
            }
            else if (message.getPayload() instanceof Country){
                countryAggregated.setCountry((Country) message.getPayload());
            }
        }

        return countryAggregated;
    }
}
