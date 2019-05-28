package com.example.demo.services;

import com.example.demo.models.Twitt;
import com.example.demo.repositories.TwittRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/twitts")
public class TwittService {

    @Autowired
    private TwittRepository twittRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Twitt> getAllTwitts(){
        return twittRepository.findAll();
    }

}
