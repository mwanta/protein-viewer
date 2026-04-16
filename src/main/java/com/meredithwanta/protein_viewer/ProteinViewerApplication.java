package com.meredithwanta.protein_viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProteinViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProteinViewerApplication.class, args);
	}

}
