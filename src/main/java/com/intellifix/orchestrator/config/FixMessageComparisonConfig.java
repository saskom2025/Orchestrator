package com.intellifix.orchestrator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.analysis.comparison")
public class FixMessageComparisonConfig {
    private List<String> messageTypes;
}
