package com.studentportal.helpbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@SpringBootApplication
public class StudentPortalApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentPortalApplication.class, args);
	}
}