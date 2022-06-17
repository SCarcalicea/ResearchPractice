package com.example.researchpractice.model.files;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Sense {
    String isn;
    String prefixMetCode;
    String naam;
    String plaats;
    String jaar;
    String waardering;
}
