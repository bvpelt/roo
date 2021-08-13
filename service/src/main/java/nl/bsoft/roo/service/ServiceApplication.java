package nl.bsoft.roo.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
//@PropertySource(value = {"application.properties", "application.properties"})
public class ServiceApplication {

    public static void main(String[] args) {
    	SpringApplication.run(ServiceApplication.class, args);
    }

}
