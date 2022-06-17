package com.example.researchpractice.model.preloader;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DBPreloaderChecker {

    // If the files have already been parsed then there is no need to parse them again
    public boolean skipDBInit;
}
