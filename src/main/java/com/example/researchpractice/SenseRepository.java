package com.example.researchpractice;

import com.example.researchpractice.model.Sense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SenseRepository extends MongoRepository<Sense, String> {

    List<Sense> findAllByNaamAndPlaatsOrderByJaar(String naam, String plaats);

}
