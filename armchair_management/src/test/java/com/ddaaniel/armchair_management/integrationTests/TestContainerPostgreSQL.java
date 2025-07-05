package com.ddaaniel.armchair_management.integrationTests;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerPostgreSQL {

    static PostgreSQLContainer<?> postgresUnderTest =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");



    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresUnderTest::getJdbcUrl);
        registry.add("spring.datasource.username", postgresUnderTest::getUsername);
        registry.add("spring.datasource.password", postgresUnderTest::getPassword);
    }


}
