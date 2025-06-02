package com.dobrev.urlstatsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class UrlStatsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlStatsServiceApplication.class, args);
	}

}
