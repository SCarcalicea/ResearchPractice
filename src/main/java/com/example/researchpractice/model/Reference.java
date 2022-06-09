package com.example.researchpractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reference{
    public String issue;
    public String key;
    @JsonProperty("doi-asserted-by")
    public String doiAssertedBy;
    @JsonProperty("first-page")
    public String firstPage;
    @JsonProperty("DOI")
    public String dOI;
    @JsonProperty("article-title")
    public String articleTitle;
    public String volume;
    public String author;
    public String year;
    @JsonProperty("journal-title")
    public String journalTitle;
    @JsonProperty("issn-type")
    public String issnType;
    @JsonProperty("ISSN")
    public String iSSN;
}