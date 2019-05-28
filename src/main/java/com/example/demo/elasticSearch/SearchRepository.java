package com.example.demo.elasticSearch;

import com.example.demo.models.Twitt;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends ElasticsearchRepository<Twitt, String> {
    Iterable<Twitt> findByText(String text);
    Iterable<Twitt> findByUserName(String text);
}
