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
    @NonNull
    private String questionStr;
    private String cleanQuestionStr;
    private Language language;
    private List<String> words;
    private List<String> wShingles;
    private List<String> wShinglesWithStopwords;
    private List<String> ontologyProperties;
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
    private List<String> literals;

    private CoreDocument nlpAnnotations;

    private List<TemplateRated> templateCandidates;
    private List<Query> queryCandidates;
    private Answer answer;


    public enum properties {
        COUNT,
        ASC_ORDERED,
        DESC_ORDERED,
        FILTER,
        ASK_QUERY,
        ORDER_BY_BYTESIZE,
        ORDER_BY_ISSUED
    }


    @JsonCreator
    public Question(@JsonProperty("question") String questionStr) {
        this.questionStr = questionStr.strip();
        this.cleanQuestionStr = this.questionStr.replaceAll("\".*\"", "").replaceAll("'.*'", "").replaceAll("[\\-.?¿!,;\"']", "");
        this.words = new ArrayList<>();
        this.wShingles = new ArrayList<>();
        this.wShinglesWithStopwords = new ArrayList<>();
        this.ontologyProperties = new ArrayList<>();
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
        this.literals = new ArrayList<>();


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
                ", cleanQuestionStr='" + cleanQuestionStr + '\'' +
                ", language=" + language +
                ", words=" + words +
                ", wShingles=" + wShingles +
                ", wShinglesWithStopwords=" + wShinglesWithStopwords +
                ", ontologyProperties=" + ontologyProperties +
                ", ontologyClasses=" + ontologyClasses +
                ", locationEntities=" + locationEntities +
                ", languageEntities=" + languageEntities +
                ", themeEntities=" + themeEntities +
                ", licenseEntities=" + licenseEntities +
                ", filetypeEntities=" + filetypeEntities +
                ", timeEntities=" + timeEntities +
                ", timeIntervalEntities=" + timeIntervalEntities +
                ", frequencyEntities=" + frequencyEntities +
                ", additionalProperties=" + additionalProperties +
                ", stringLiterals=" + literals +
                ", answer=" + answer +
                '}';
    }
}
