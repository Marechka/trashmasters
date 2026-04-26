package com.app.trashmasters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrashmastersApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrashmastersApplication.class, args);
	}

}
