package com.example.demo.elasticSearch;

import com.example.demo.models.Twitt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchService {

    @Autowired
    SearchRepository searchRepository;

    @GetMapping(value = "/all")
    public Iterable<Twitt> getAll(){
        return searchRepository.findAll();
    }

    @GetMapping(value = "/content/{text}")
    public Iterable<Twitt> getByContent(@PathVariable final String text){
        return searchRepository.findByText(text);
    }

    @GetMapping(value = "/user/{name}")
    public Iterable<Twitt> getByUser(@PathVariable final String name){
        return searchRepository.findByUserName(name);
    }
}
