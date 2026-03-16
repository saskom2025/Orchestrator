package com.intellifix.orchestrator.redis.strategy;

import org.springframework.stereotype.Component;

@Component("D")
public class NewOrderSingleStrategy implements FixMessageStrategy {
    @Override
    public boolean isEligible(String senderCompId, String targetCompId) {
        return targetCompId.equals(senderCompId);
    }

    @Override
    public String getMessageType() {
        return "D";
    }
}
