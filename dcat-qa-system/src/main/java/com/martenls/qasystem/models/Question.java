package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pemistahl.lingua.api.Language;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.Pair;
import lombok.Data;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
public class Question {
    @NonNull private String questionStr;
    private Language language;
    private List<String> words;
    private Map<String, String> posTags;
    private List<String> wShingles;
    private List<String> wShinglesWithStopwords;
    private Set<String> ontologyProperties;
    private Set<String> ontologyClasses;
    private Set<String> locationEntities;
    private Set<String> languageEntities;
    private Set<String> themeEntities;
    private Set<String> licenseEntities;
    private Set<String> filetypeEntities;
    private List<Calendar> timeEntities;
    private List<Pair<Calendar, Calendar>> timeIntervalEntities;
    private Set<String> frequencyEntities;
    private Set<Question.properties> additionalProperties;
    private List<String> stringLiterals;

    private CoreDocument nlpAnnotations;

    private List<TemplateRated> templateCandidates;
    private List<Query> queryCandidates;
    private Answer answer;


    public enum properties {
        COUNT,
        ASC_ORDERED,
        DESC_ORDERED,
        FILTER,
        ASK_QUERY
    }


    @JsonCreator
    public Question(@JsonProperty("question") String questionStr) {
        this.questionStr = questionStr;
        this.words = new ArrayList<>();
        this.posTags = new HashMap<>();
        this.wShingles = new ArrayList<>();
        this.wShinglesWithStopwords = new ArrayList<>();
        this.ontologyProperties = new HashSet<>();
        this.ontologyClasses = new HashSet<>();
        this.locationEntities = new HashSet<>();
        this.languageEntities = new HashSet<>();
        this.themeEntities = new HashSet<>();
        this.licenseEntities = new HashSet<>();
        this.filetypeEntities = new HashSet<>();
        this.timeEntities = new ArrayList<>();
        this.timeIntervalEntities = new ArrayList<>();
        this.frequencyEntities = new HashSet<>();
        this.additionalProperties = new HashSet<>();
        this.stringLiterals = new ArrayList<>();


        this.templateCandidates = new ArrayList<>();
        this.queryCandidates = new ArrayList<>();
    }

    public boolean hasProperty(Question.properties property) {
        return this.additionalProperties.contains(property);
    }

    public List<String> getEntities() {
        return Stream.of(this.ontologyClasses, this.locationEntities, this.languageEntities, this.themeEntities, this.licenseEntities, this.filetypeEntities, this.frequencyEntities).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public int getEntityCount() {
        return this.ontologyClasses.size() + this.locationEntities.size() + this.languageEntities.size() + this.themeEntities.size() + this.licenseEntities.size() + this.filetypeEntities.size() + this.timeEntities.size() + this.frequencyEntities.size();
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionStr='" + questionStr + '\'' +
                ",\n language='" + language + '\'' +
                ",\n words=" + words +
                ",\n posTags=" + posTags +
                ",\n wShingles=" + wShingles +
                ",\n ontologyProperties=" + ontologyProperties +
                ",\n ontologyClasses=" + ontologyClasses +
                ",\n locationEntities=" + locationEntities +
                ",\n additionalProperties=" + additionalProperties +
                ",\n stringLiterals=" + stringLiterals +
                ",\n templateCandidates=" + templateCandidates +
                ",\n queryCandidates=" + queryCandidates +
                ",\n answer='" + answer + '\'' +
                '}';
    }
}
