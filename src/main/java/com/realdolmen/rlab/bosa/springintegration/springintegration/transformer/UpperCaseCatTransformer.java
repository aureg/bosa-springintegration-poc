package com.realdolmen.rlab.bosa.springintegration.springintegration.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.model.CatFact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpperCaseCatTransformer {
    @Transformer
    public CatFact exampleTransformer(CatFact payload) {
        log.error("TRANSFORM__________________________________UpperCaseCatTransformer");

        String fact = payload.getFact();
        String newFact = fact.toUpperCase();
        payload.setFact(newFact);
        payload.setLength(newFact.length());
        return payload;
    }
}
