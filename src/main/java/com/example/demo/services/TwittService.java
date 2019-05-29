package com.example.demo.services;

import com.example.demo.elasticSearch.SearchRepository;
import com.example.demo.kafka.TwitterKafkaProducer;
import com.example.demo.models.Twitt;
import com.example.demo.repositories.TwittRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/twitt")
public class TwittService {

    @Autowired
    private TwittRepository twittRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private TwitterKafkaProducer twitterKafkaProducer;

    @RequestMapping(value = "/{id", method = RequestMethod.GET)
    @ResponseBody
    public Twitt getTwittById(@PathVariable String id){
        return this.twittRepository.findTweetById(id);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public List<Twitt> getAllTwitts(){
        return twittRepository.findAll();
    }

    @RequestMapping(value = "/startKafka", method = RequestMethod.GET)
    public void start(){

        List<String> hashtags = new ArrayList<>();

        hashtags.add("isapre");
        hashtags.add("consalud");
        hashtags.add("salud");

        this.twitterKafkaProducer.run(this.twittRepository, this.searchRepository, hashtags);
    }

    @RequestMapping(value = "/stopKafka", method = RequestMethod.GET)
    public List<Twitt> stop(){
        this.twitterKafkaProducer.stop();
        return twittRepository.findAll();
    }

}
