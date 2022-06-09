package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ContentDomain{
    public ArrayList<String> domain;
    @JsonProperty("crossmark-restriction")
    public boolean crossmarkRestriction;
}