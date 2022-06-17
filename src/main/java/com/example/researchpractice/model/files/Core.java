package com.example.researchpractice.model.files;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Core {
    String id;
    String title;
    String acronym;
    String coreYear;
    String classInfo;
    String isSomething;
    String subId;
    String subSubId;

    // Extra tags
    List<String> extras;
}
