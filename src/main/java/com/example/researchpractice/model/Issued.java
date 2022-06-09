package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Issued{
    @JsonProperty("date-parts")
    public ArrayList<ArrayList<Integer>> dateParts;
}