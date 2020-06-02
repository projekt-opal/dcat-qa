package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.util.*;


@Data
public class Question {
    @NonNull private String questionStr;
    private String language;
    private List<String> words;
    private Map<String, String> posTags;
    private List<String> wShingles;
    private Set<String> ontologyProperties;
    private Set<String> ontologyClasses;
    private Set<String> locationEntities;
    private Set<Question.properties> additionalProperties;
    private List<String> stringLiterals;

    private List<TemplateRated> templateCandidates;
    private List<Query> queryCandidates;
    private String answer;

    public enum properties {
        COUNT,
        ASC_ORDERED,
        DESC_ORDERED,
        FILTER,
    }


    @JsonCreator
    public Question(@JsonProperty("question") String questionStr) {
        this.questionStr = questionStr;
        this.words = new ArrayList<>();
        this.posTags = new HashMap<>();
        this.wShingles = new ArrayList<>();
        this.ontologyProperties = new HashSet<>();
        this.ontologyClasses = new HashSet<>();
        this.locationEntities = new HashSet<>();
        this.additionalProperties = new HashSet<>();
        this.stringLiterals = new ArrayList<>();

        this.templateCandidates = new ArrayList<>();
        this.queryCandidates = new ArrayList<>();
    }

    public boolean hasProperty(Question.properties property) {
        return this.additionalProperties.contains(property);
    }


    @Override
    public String toString() {
        return "Question{" +
                "question='" + questionStr + '\'' +
                ",\n language='" + language + '\'' +
                ",\n words=" + words +
                ",\n posTags=" + posTags +
                ",\n wShingles=" + wShingles +
                ",\n ontologyProperties=" + ontologyProperties +
                ",\n ontologyClasses=" + ontologyClasses +
                ",\n locations=" + locationEntities +
                '}';
    }
}
