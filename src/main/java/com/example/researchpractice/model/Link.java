package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link{
    @JsonProperty("URL")
    public String uRL;
    @JsonProperty("content-type")
    public String contentType;
    @JsonProperty("content-version")
    public String contentVersion;
    @JsonProperty("intended-application")
    public String intendedApplication;
}