package nl.bsoft.roo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "nl.bsoft.roo")
@EnableJpaRepositories(basePackages = "nl.bsoft.roo")
//@PropertySource(value = {"storage-application.properties", "application.properties"})
public class RooApplication {

    public static void main(String[] args) {
        SpringApplication.run(RooApplication.class, args);
    }

}
