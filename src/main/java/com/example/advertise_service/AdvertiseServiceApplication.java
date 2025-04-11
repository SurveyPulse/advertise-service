package com.example.advertise_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		DataSourceAutoConfiguration.class  // 이 옵션을 추가하면 JPA 및 데이터소스 자동 구성이 제외됩니다.
})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.example.advertise_service", "com.example.global"})
@EnableReactiveMongoRepositories(basePackages = "com.example.advertise_service.repository")
public class AdvertiseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvertiseServiceApplication.class, args);
	}

}
