package br.com.segmedic.clubflex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class ClubflexApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(ClubflexApplication.class, args);
	}
}
