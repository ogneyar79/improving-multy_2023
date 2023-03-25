package com.kafkaservice.runner.impl;


import com.kafkaservice.exception.TwitterToKafkaException;
import com.kafkaservice.listener.TwitterKafkaStatusListener;
import com.kafkaservice.runner.StreamRunner;

import com.maksimus.vss.app.config.ToKafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import javax.inject.Qualifier;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component("MockKafkaStream")
@ConditionalOnProperty(name = "to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);
    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[]{
            "Maksim",
            "Robert",
            "Robot",
            "Hamlet",
            "trouble", "dangerous", "crash",
            "Hope",
            "Elena",
            "Victor",
            "Basilio",
            "magna",
            "Rub", "dollar", "EUr",
            "White", "RED", "Black",
            "Sun", "Son",
            "Dream", "Freedom",
            "flat",
            "decision",
            "solution", "Happiness"
    };
    private final static String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";
    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private final ToKafkaConfigData toKafkaConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;


    public MockKafkaStreamRunner(ToKafkaConfigData toKafkaConfigData, TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.toKafkaConfigData = toKafkaConfigData;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        String[] keywords = toKafkaConfigData.getToKafkaSourceList().toArray(new String[0]);
        int minTweetLength = toKafkaConfigData.getMockMinTweetLength();
        int maxTweetLength = toKafkaConfigData.getMockMaxTweetLength();
        long sleepTimeMs = toKafkaConfigData.getMockSleepMs();
        LOG.info("Starting mock filter twitter streams for keywords {}", Arrays.toString(keywords));

        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);

    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                LOG.error("Error creating twitter status!", e);
            }
        });
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new TwitterToKafkaException("Error while sleeping for waiting new status to create");
        }
    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;

        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(keywords, tweet, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }
}
