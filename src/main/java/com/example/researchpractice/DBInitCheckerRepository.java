package com.example.researchpractice;

import com.example.researchpractice.model.preloader.DBPreloaderChecker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DBInitCheckerRepository extends MongoRepository<DBPreloaderChecker, Integer> {
}
