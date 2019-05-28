package com.example.demo.repositories;

import com.example.demo.models.Twitt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwittRepository extends MongoRepository<Twitt, String> {
    Twitt findTweetById(String id);
}
