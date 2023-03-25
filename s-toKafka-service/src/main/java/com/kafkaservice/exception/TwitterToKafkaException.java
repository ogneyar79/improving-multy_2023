package com.kafkaservice.exception;

import org.springframework.stereotype.Component;

@Component
public class TwitterToKafkaException  extends RuntimeException{

    public TwitterToKafkaException() {
        super();
    }

    public TwitterToKafkaException(String message) {
        super(message);
    }

    public TwitterToKafkaException(String message, Throwable cause) {
        super(message, cause);
    }

}
