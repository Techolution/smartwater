package com.techolution.mauritius.smartwater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ConsolidateddetailsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsolidateddetailsApplication.class, args);
	}
}
