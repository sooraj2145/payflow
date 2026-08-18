package com.sooraj.payflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
class PayflowApplicationTests {

	@Test
	void contextLoads() {
	}

	@Configuration
	static class TestContainersConfig {

		@Bean
		@ServiceConnection
		PostgreSQLContainer<?> postgresContainer() {
			return new PostgreSQLContainer<>("postgres:17");
		}
	}

}
