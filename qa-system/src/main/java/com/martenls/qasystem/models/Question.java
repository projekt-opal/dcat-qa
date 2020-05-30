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
    private Set<Question.properties> additionProperties;
    private List<TemplateRated> templateCandidates;
    private List<Query> queryCandidates;
    private String answer;

    public enum properties {
        COUNT,
        ASC_ORDERED,
        DESC_ORDERED,
        FILTER,

    }

    public Question(String question) {
        this.question = question;

        this.words = new ArrayList<>();
        this.posTags = new HashMap<>();
        this.wShingles = new ArrayList<>();
        this.ontologyProperties = new HashSet<>();
        this.ontologyClasses = new HashSet<>();
        this.locations = new HashSet<>();
        this.additionProperties = new HashSet<>();
        this.templateCandidates = new ArrayList<>();
        this.queryCandidates = new ArrayList<>();
    }

    public boolean hasProperty(Question.properties property) {
        return this.additionProperties.contains(property);
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
