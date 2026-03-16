package com.intellifix.orchestrator.redis.strategy;

public interface FixMessageStrategy {
    boolean isEligible(String senderCompId, String targetCompId);

    String getMessageType();
}
