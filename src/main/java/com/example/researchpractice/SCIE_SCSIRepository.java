package com.example.researchpractice;

import com.example.researchpractice.model.Scie_ssci;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SCIE_SCSIRepository extends MongoRepository<Scie_ssci, String> {

    List<Scie_ssci> findAllByIssnLike(String issn);
}
