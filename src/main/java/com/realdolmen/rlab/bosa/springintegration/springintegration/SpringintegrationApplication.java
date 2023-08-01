package com.realdolmen.rlab.bosa.springintegration.springintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

import java.io.IOException;

//@SpringBootApplication
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@IntegrationComponentScan
//@ImportResource("/integration/integration.xml")
public class SpringintegrationApplication {

    public static void main(String[] args) throws IOException {

        ConfigurableApplicationContext ctx = SpringApplication.run(SpringintegrationApplication.class, args);

        System.out.println("Hit Enter to terminate");
        System.in.read();

        ctx.close();
    }

}
