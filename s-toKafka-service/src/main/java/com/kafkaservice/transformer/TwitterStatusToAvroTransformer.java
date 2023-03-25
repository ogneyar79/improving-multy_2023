package com.kafkaservice.transformer;


import org.springframework.stereotype.Component;
import twitter4j.Status;


@Component
public class TwitterStatusToAvroTransformer {

    public com.maksimus.vss.improving.kafka.avro.model.TwitterAvroModel getTwitterAvroModelFromStatus(Status status) {
        return com.maksimus.vss.improving.kafka.avro.model.TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }

}
