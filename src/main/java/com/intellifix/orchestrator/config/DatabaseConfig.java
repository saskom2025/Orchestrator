package com.intellifix.orchestrator.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        // We use a property source or environment variable for the URL
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getProperty("spring.datasource.url");
        }

        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            log.info("Detected postgresql:// prefix without jdbc: prefix. Prepending jdbc: prefix.");
            properties.setUrl("jdbc:" + databaseUrl);
        } else {
            properties.setUrl(databaseUrl);
        }

        // Pass through username and password if they are set in the environment
        properties.setUsername(System.getenv("DB_USERNAME"));
        properties.setPassword(System.getenv("DB_PASSWORD"));

        return properties;
    }
}
