package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class License{
    public Start start;
    @JsonProperty("content-version")
    public String contentVersion;
    @JsonProperty("delay-in-days")
    public int delayInDays;
    @JsonProperty("URL")
    public String uRL;
}