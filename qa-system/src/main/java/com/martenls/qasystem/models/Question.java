package com.martenls.qasystem.models;

import lombok.Data;
import lombok.NonNull;

import java.util.*;


@Data
public class Question {
    @NonNull private String question;
    private String language;
    private List<String> words;
    private Map<String, String> posTags;
    private List<String> wShingles;
    private Set<String> ontologyProperties;
    private Set<String> ontologyClasses;
    private Set<String> locations;

    public Question(String question) {
        this.question = question;

        this.words = new ArrayList<>();
        this.posTags = new HashMap<>();
        this.wShingles = new ArrayList<>();
        this.ontologyProperties = new HashSet<>();
        this.ontologyClasses = new HashSet<>();
        this.locations = new HashSet<>();
    }


    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ",\n language='" + language + '\'' +
                ",\n words=" + words +
                ",\n posTags=" + posTags +
                ",\n wShingles=" + wShingles +
                ",\n ontologyProperties=" + ontologyProperties +
                ",\n ontologyClasses=" + ontologyClasses +
                ",\n locations=" + locations +
                '}';
    }
}
