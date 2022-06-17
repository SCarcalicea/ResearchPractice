package com.example.researchpractice.model.basemodel;

import com.example.researchpractice.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class DxDoi{
    public Indexed indexed;
    @JsonProperty("reference-count")
    public int referenceCount;
    public String publisher;
    public String event;
    @JsonProperty("publisher-location")
    public String publisherLocation;
    public ArrayList<License> license;
    @JsonProperty("content-domain")
    public ContentDomain contentDomain;
    @JsonProperty("published-print")
    public PublishedPrint publishedPrint;
    @JsonProperty("DOI")
    public String dOI;
    public String type;
    public Created created;
    public String page;
    @JsonProperty("update-policy")
    public String updatePolicy;
    public String source;
    @JsonProperty("is-referenced-by-count")
    public int isReferencedByCount;
    public String title;
    public String prefix;
    public String volume;
    public ArrayList<Author> author;
    public String member;
    public ArrayList<Reference> reference;
    @JsonProperty("container-title")
    public Object containerTitle;
    @JsonProperty("original-title")
    public ArrayList<Object> originalTitle;
    public String language;
    public ArrayList<Link> link;
    public Deposited deposited;
    public int score;
    public Resource resource;
    public ArrayList<Object> subtitle;
    @JsonProperty("short-title")
    public ArrayList<Object> shortTitle;
    public Issued issued;
    @JsonProperty("references-count")
    public int referencesCount;
    @JsonProperty("alternative-id")
    public ArrayList<String> alternativeId;
    @JsonProperty("URL")
    public String uRL;
    public Relation relation;
    @JsonProperty("ISSN")
    public ArrayList<String> iSSN;
    public ArrayList<String> subject;
    @JsonProperty("container-title-short")
    public String containerTitleShort;
    public Published published;
    public ArrayList<Assertion> assertion;
}


