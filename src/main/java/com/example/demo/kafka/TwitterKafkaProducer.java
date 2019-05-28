package com.example.demo.kafka;

import com.google.gson.Gson;
import com.example.demo.elasticSearch.SearchRepository;
import com.example.demo.repositories.TwittRepository;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;



import com.example.demo.configurations.kafkaConfiguration;
import com.example.demo.configurations.twitterConfiguration;
import com.example.demo.models.Twitt;
import com.example.demo.kafka.CallBack;


import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RestController
@Configurable
@RequestMapping(value = "/kafkaProducer")
public class TwitterKafkaProducer {

    private Client client;
    private BlockingQueue<String> queue;
    private Gson gson;
    private CallBack callback;
    private Authentication authentication;

    public TwitterKafkaProducer() {
        this.authentication = new OAuth1(
                twitterConfiguration.CONSUMER_KEY,
                twitterConfiguration.CONSUMER_SECRET,
                twitterConfiguration.ACCESS_TOKEN,
                twitterConfiguration.TOKEN_SECRET);

        queue = new LinkedBlockingQueue<>(10000);
        gson = new Gson();
        callback = new CallBack();
    }

    private Producer<Long, String> getProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.SERVERS);
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 500);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

    public void run(TwittRepository twittRepository, SearchRepository searchRepository, List<String> hashtags) {

        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(hashtags);
        client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .authentication(authentication)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();

        try (Producer<Long, String> producer = getProducer()) {
            while (true) {

                Twitt twitt = gson.fromJson(queue.take(), Twitt.class);
                Twitt twittAux = new Twitt(twitt.getId(),twitt.getText(),twitt.getLang(),twitt.getUser(),twitt.getRetweetCount(),twitt.getFavoriteCount());

                twittRepository.save(twittAux);
                searchRepository.save(twittAux);

                String keyLong = twitt.getId();
                long key = Long.parseLong(keyLong);
                String msg = twitt.toString();
                ProducerRecord<Long, String> record = new ProducerRecord<>(kafkaConfiguration.TOPIC, key, msg);
                producer.send(record, callback);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }

    public void stop() {client.stop();}
}
