package com.deep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// This allows JPA auditing for populating the created at and updated at
@EnableJpaAuditing
public class AirlineCoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlineCoreServiceApplication.class, args);
	}

}
