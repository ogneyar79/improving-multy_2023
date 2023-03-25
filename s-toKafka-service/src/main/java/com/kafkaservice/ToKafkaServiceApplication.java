package com.kafkaservice;


import com.kafkaservice.init.StreamInitializer;
import com.kafkaservice.listener.TwitterKafkaStatusListener;
import com.kafkaservice.runner.StreamRunner;
import com.kafkaservice.runner.impl.MockKafkaStreamRunner;
import com.maksimus.vss.app.config.ToKafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties(ToKafkaConfigData.class)
@SpringBootApplication
public class ToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ToKafkaServiceApplication.class);

    private final StreamRunner streamRunner;

    private final StreamInitializer streamInitializer;


    @Autowired
    public ToKafkaServiceApplication(StreamRunner streamRunner, StreamInitializer streamInitializer) {
        this.streamRunner = streamRunner;
        this.streamInitializer = streamInitializer;
    }

    public static void main(String... args) {
        SpringApplication.run(ToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("App starting.....");
        streamInitializer.init();
        streamRunner.start();
    }
}
