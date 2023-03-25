package com.maksimus.vss.app.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "to-kafka-service")
@Data
@ComponentScan("com.maksimus.vss")
public class ToKafkaConfigData {

    private  List<String> toKafkaSourceList;
    private  List<String> toBeach;
    private Boolean enableMockTweets;
    private Long  mockSleepMs;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;
}
