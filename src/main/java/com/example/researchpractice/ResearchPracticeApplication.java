package com.example.researchpractice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ResearchPracticeApplication {

    @Autowired
    public SCIE_SCSIRepository scie_scsiRepository;
    @Autowired
    public SenseRepository senseRepository;
    @Autowired
    public CoreRepository coreRepository;
    @Autowired
    public DBInitCheckerRepository dbChecker;

    public static void main(String[] args) {
        SpringApplication.run(ResearchPracticeApplication.class, args);
    }

    @PostConstruct
    public void init() {
        DataBasePreloader.builder()
                .coreRepository(coreRepository)
                .dbChecker(dbChecker)
                .scie_scsiRepository(scie_scsiRepository)
                .senseRepository(senseRepository)
                .build()
                .init();
    }
}
