package nl.bsoft.roo.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@PropertySource(value = "classpath:storage-application.properties")
public class StorageApplication {

    public static void main(String[] args) {

        SpringApplication.run(StorageApplication.class, args);
    }

}
