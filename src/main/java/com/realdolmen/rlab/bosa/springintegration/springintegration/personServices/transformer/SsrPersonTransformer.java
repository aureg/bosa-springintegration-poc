package com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.transformer;

import com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.domain.Address;
import com.realdolmen.rlab.bosa.springintegration.springintegration.personServices.domain.Person;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetSsrPersonRequest;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.GetSsrPersonResponse;
import com.realdolmen.rlab.bosa.springintegration.springintegration.wsdl.SsrPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SsrPersonTransformer {

    @Transformer
    public GetSsrPersonRequest toGetSsrPersonRequest(Object payload) {
        log.error("TRANSFORM__________________________________ Person to SsrPersonRequest");
        GetSsrPersonRequest getSsrPersonRequest = new GetSsrPersonRequest();

        if (payload instanceof Person) {
            Person personFromRequest = (Person) payload;
            if (personFromRequest != null && personFromRequest.getNiss() != null) {
                getSsrPersonRequest.setNiss(personFromRequest.getNiss().toString());
                return getSsrPersonRequest;
            }
        } else if (payload instanceof String) {
            getSsrPersonRequest.setNiss((String) payload);
        } else if (payload instanceof GetSsrPersonRequest) {
            getSsrPersonRequest = (GetSsrPersonRequest) payload;
        }
        log.error("TRANSFORM__________________________________ niss : " + getSsrPersonRequest.getNiss());
        return getSsrPersonRequest;
    }


    @Transformer
    public Person toPerson(Object payload) {
        log.error("TRANSFORM__________________________________ GetSsrPersonResponse to Person");
        Person person = new Person();

        if (payload instanceof GetSsrPersonResponse) {
            GetSsrPersonResponse personFromRequest = (GetSsrPersonResponse) payload;
            SsrPerson nrPerson = personFromRequest.getSsrPerson();

            Address address = new Address()
                    .setStreet(nrPerson.getAddress().getStreet())
                    .setNumber(nrPerson.getAddress().getNumber())
                    .setPostalcode(nrPerson.getAddress().getPostalcode())
                    .setCity(nrPerson.getAddress().getCity())
                    .setCountry(nrPerson.getAddress().getCountry());
            person.setLastname(nrPerson.getLastname())
                    .setFirstname(nrPerson.getFirstname())
                    .setNiss(Long.parseLong(nrPerson.getNiss()))
                    .setEmail("")
                    .setAddress(address);
        }

        log.error("TRANSFORM__________________________________ niss : " + person.getNiss());
        return person;
    }
}
