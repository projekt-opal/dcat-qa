package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pemistahl.lingua.api.Language;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.Pair;
import lombok.Data;
import lombok.Getter;
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
    private Set<String> ontologyProperties;
    private Set<String> ontologyClasses;
    private Set<String> locationEntities;
    private Set<String> languageEntities;
    private Set<String> themeEntities;
    private Set<String> fileFormatEntities;
    private List<Calendar> timeEntities;
    private List<Pair<Calendar, Calendar>> timeIntervalEntities;
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
        this.languageEntities = new HashSet<>();
        this.themeEntities = new HashSet<>();
        this.fileFormatEntities = new HashSet<>();
        this.timeEntities = new ArrayList<>();
        this.timeIntervalEntities = new ArrayList<>();
        this.additionalProperties = new HashSet<>();
        this.stringLiterals = new ArrayList<>();



        this.templateCandidates = new ArrayList<>();
        this.queryCandidates = new ArrayList<>();
    }

    public boolean hasProperty(Question.properties property) {
        return this.additionalProperties.contains(property);
    }

    public List<String> getEntities() {
        return Stream.of(this.locationEntities, this.languageEntities, this.themeEntities, this.fileFormatEntities).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public int getEntityCount() {
        return this.locationEntities.size() + this.languageEntities.size() + this.themeEntities.size() + this.fileFormatEntities.size() + this.timeEntities.size();
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
