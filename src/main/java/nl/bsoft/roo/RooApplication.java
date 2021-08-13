package nl.bsoft.roo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "nl.bsoft.roo")
@EnableJpaRepositories(basePackages = "nl.bsoft.roo")
public class RooApplication {

	public static void main(String[] args) {
		SpringApplication.run(RooApplication.class, args);
	}

}
