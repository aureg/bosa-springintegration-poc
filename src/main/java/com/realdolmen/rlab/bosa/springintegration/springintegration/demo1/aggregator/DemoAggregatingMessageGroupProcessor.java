package com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.aggregator;

import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model.CatFact;
import com.realdolmen.rlab.bosa.springintegration.springintegration.demo1.model.CountryAggregated;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.aggregator.AbstractAggregatingMessageGroupProcessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import java.util.Map;

@Slf4j
public class DemoAggregatingMessageGroupProcessor extends AbstractAggregatingMessageGroupProcessor {
    @Override
    protected CountryAggregated aggregatePayloads(MessageGroup group, Map<String, Object> defaultHeaders) {

        log.error("AGGREGATOR_________________________________ DEMO > aggregatePayloads");
        CountryAggregated countryAggregated = new CountryAggregated();

        for (Message message : group.getMessages()) {
            if (message.getPayload() instanceof String) {
                countryAggregated.setExtraString((String) message.getPayload());
            } else if (message.getPayload() instanceof Country) {
                countryAggregated.setCountry((Country) message.getPayload());
            } else if (message.getPayload() instanceof CatFact) {
                countryAggregated.setCatFactAsString(((CatFact) message.getPayload()).getFact());
            }
        }

        return countryAggregated;
    }
}
