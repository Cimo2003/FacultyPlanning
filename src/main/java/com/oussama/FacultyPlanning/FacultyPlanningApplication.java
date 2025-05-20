package com.oussama.FacultyPlanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FacultyPlanningApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacultyPlanningApplication.class, args);
	}

}
