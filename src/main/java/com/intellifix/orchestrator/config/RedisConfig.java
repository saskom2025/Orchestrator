package com.intellifix.orchestrator.config;

import com.intellifix.orchestrator.model.SimulationDTO;
import com.intellifix.orchestrator.redis.RedisStreamConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${redis.simulation.stream.name:fix_stream}")
    private String streamName;

    @Value("${redis.simulation.consumer.group:fix_consumer}")
    private String consumerGroup;

    @Value("${redis.status.stream.name:status_stream}")
    private String statusStreamName;

    @Value("${redis.status.consumer.group:status_consumer}")
    private String statusConsumerGroup;

    @Value("${redis.health.stream.name:fix_health_stream}")
    private String healthStreamName;

    @Value("${redis.health.consumer.group:health_consumer}")
    private String healthConsumerGroup;

    @Value("${redis.log.stream.name:fix_log_stream}")
    private String logStreamName;

    @Value("${redis.log.consumer.group:log_consumer}")
    private String logConsumerGroup;

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory factory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        return StreamMessageListenerContainer.create(factory, options);
    }

    @Bean
    public Subscription simulationSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            RedisStreamConsumer consumer, RedisTemplate<String, Object> redisTemplate) {

        initStreamGroup(redisTemplate, streamName, consumerGroup);

        Subscription subscription = container.receive(
                Consumer.from(consumerGroup, "instance-1"),
                StreamOffset.create(streamName, ReadOffset.lastConsumed()),
                consumer);

        container.start();
        log.info("Redis Simulation Stream Subscription started: {} -> {}", streamName, consumerGroup);
        return subscription;
    }

    @Bean
    public Subscription statusSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            com.intellifix.orchestrator.redis.StatusStreamListener listener,
            RedisTemplate<String, Object> redisTemplate) {

        initStreamGroup(redisTemplate, statusStreamName, statusConsumerGroup);

        Subscription subscription = container.receive(
                Consumer.from(statusConsumerGroup, "instance-1"),
                StreamOffset.create(statusStreamName, ReadOffset.lastConsumed()),
                listener);

        log.info("Redis Status Stream Subscription started: {} -> {}", statusStreamName, statusConsumerGroup);
        return subscription;
    }

    @Bean
    public Subscription healthSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            com.intellifix.orchestrator.redis.HealthStreamListener listener,
            RedisTemplate<String, Object> redisTemplate) {

        initStreamGroup(redisTemplate, healthStreamName, healthConsumerGroup);

        Subscription subscription = container.receive(
                Consumer.from(healthConsumerGroup, "instance-1"),
                StreamOffset.create(healthStreamName, ReadOffset.lastConsumed()),
                listener);

        log.info("Redis Health Stream Subscription started: {} -> {}", healthStreamName, healthConsumerGroup);
        return subscription;
    }

    @Bean
    public Subscription logSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            com.intellifix.orchestrator.redis.FixLogStreamListener listener,
            RedisTemplate<String, Object> redisTemplate) {

        initStreamGroup(redisTemplate, logStreamName, logConsumerGroup);

        Subscription subscription = container.receive(
                Consumer.from(logConsumerGroup, "instance-1"),
                StreamOffset.create(logStreamName, ReadOffset.lastConsumed()),
                listener);

        log.info("Redis Log Stream Subscription started: {} -> {}", logStreamName, logConsumerGroup);
        return subscription;
    }

    private void initStreamGroup(RedisTemplate<String, Object> redisTemplate, String stream, String group) {
        try {
            redisTemplate.opsForStream().createGroup(stream, group);
            log.info("Created Redis consumer group: {} for stream: {}", group, stream);
        } catch (Exception e) {
            log.debug("Consumer group {} already exists or stream {} not yet initialized", group, stream);
        }
    }
}
