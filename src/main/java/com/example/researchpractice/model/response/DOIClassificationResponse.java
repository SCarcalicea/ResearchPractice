package com.example.researchpractice.model.response;

import lombok.Builder;

@Builder
public class DOIClassificationResponse {

    public String type;
    public Integer year;
    public String issn;
    public String containerTitle;
    public String publisher;
    public String publisherLocation;
    public String event;
    public String acronim;
    public String event_title;
    public String classCNATDCU;
    public String classINFO;
    public boolean isInWos;
    public boolean info;

}
