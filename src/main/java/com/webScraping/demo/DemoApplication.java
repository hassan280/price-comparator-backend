package com.webScraping.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//exclude = { SecurityAutoConfiguration.class }
@SpringBootApplication(scanBasePackages={
		"com.webScraping.demo.controller", "com.webScraping.demo.model"
		,"com.webScraping.demo.repositories","com.webScraping.demo.service"
		,"securityConfiguration"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
