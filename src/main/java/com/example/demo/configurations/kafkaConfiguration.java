package com.example.demo.configurations;

import org.springframework.context.annotation.Configuration;

@Configuration
public class kafkaConfiguration {
    public static final String SERVERS = "localhost:9092, localhost:9093, localhost:9094";
    public static final String TOPIC = "twitterTest";
    public static final long SLEEP_TIMER = 1000;
}
