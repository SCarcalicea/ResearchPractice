package com.example.researchpractice;

import com.example.researchpractice.model.Core;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoreRepository extends MongoRepository<Core, String> {
}
