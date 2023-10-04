package com.example.analysParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AnalysParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalysParserApplication.class, args);
	}

}
