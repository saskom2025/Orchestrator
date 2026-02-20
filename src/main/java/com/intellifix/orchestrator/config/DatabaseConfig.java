package com.intellifix.orchestrator.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DatabaseConfig {

    /*@Bean
    @Primary
    public javax.sql.DataSource dataSource(DataSourceProperties properties) {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = properties.getUrl();
        }

        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            log.info("Detected postgresql:// prefix. Prepending jdbc: prefix.");
            databaseUrl = "jdbc:" + databaseUrl;
        }

        return properties.initializeDataSourceBuilder()
                .url(databaseUrl)
                .build();
    }*/
}
