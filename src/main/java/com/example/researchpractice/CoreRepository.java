package com.example.researchpractice;

import com.example.researchpractice.model.files.Core;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CoreRepository extends MongoRepository<Core, String> {

    List<Core> findAllByAcronymOrderByCoreYear(String acronim);
}
