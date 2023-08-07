package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.domain.Person;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetNrPersonRequest;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetNrPersonResponse;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.NrPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NrPersonTransformer {

    @Transformer
    public GetNrPersonRequest toGetNrPersonRequest(Object payload) {
        log.error("TRANSFORM__________________________________ Person to NrPersonRequest");
        GetNrPersonRequest getNrPersonRequest = new GetNrPersonRequest();

        if (payload instanceof Person) {
            log.error("I'm a person");
            Person personFromRequest = (Person) payload;
            if (personFromRequest != null && personFromRequest.getNiss() != null) {
                getNrPersonRequest.setNiss(personFromRequest.getNiss().toString());

                return getNrPersonRequest;
            }
        } else if (payload instanceof String) {
            getNrPersonRequest.setNiss((String) payload);
        }

        log.error("TRANSFORM__________________________________ niss : " + getNrPersonRequest.getNiss());
        return getNrPersonRequest;
    }


    @Transformer
    public Person toPerson(Object payload) {
        log.error("TRANSFORM__________________________________ GetNrPersonResponse to Person");
        Person person = new Person();

        if (payload instanceof GetNrPersonResponse) {
            log.error("I'm a person");
            GetNrPersonResponse personFromRequest = (GetNrPersonResponse) payload;
            NrPerson nrPerson = personFromRequest.getNrPerson();

            person.setLastname(nrPerson.getLastname());
            person.setFirstname(nrPerson.getFirstname());
            person.setNiss(Long.parseLong(nrPerson.getNiss()));
            person.setEmail("");
        }

        log.error("TRANSFORM__________________________________ niss : " + person.getNiss());
        return person;
    }
}
