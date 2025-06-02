package com.leep.security.hedge;

import com.leep.security.hedge.config.model.KafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class HedgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HedgeApplication.class, args);
	}

}
