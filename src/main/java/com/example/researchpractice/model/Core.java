package com.example.researchpractice.model;

import lombok.Builder;

import java.util.List;

@Builder
public class Core {
    String id;
    String type;
    String subtype;
    String coreYear;
    String rank;
    String isSomething;
    String subId;
    String subSubId;

    // Extra tags
    List<String> extras;
}
