package com.turtlecoin.auctionservice;

import com.turtlecoin.auctionservice.global.config.RedisPrefixProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties(RedisPrefixProperties.class)
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
public class AuctionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionServiceApplication.class, args);
	}

}
