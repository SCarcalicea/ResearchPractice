package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;

public class Start{
    @JsonProperty("date-parts")
    public ArrayList<ArrayList<Integer>> dateParts;
    @JsonProperty("date-time")
    public Date dateTime;
    public long timestamp;
}