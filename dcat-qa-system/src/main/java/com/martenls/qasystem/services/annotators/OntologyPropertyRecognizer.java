package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.LabeledURIJsonParser;
import com.martenls.qasystem.services.AdditionalPropertyIndicatorsProvider;
import com.martenls.qasystem.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Enables the recognition of dcat properties and classes among words.
 */
@Log4j2
@Service
public class OntologyPropertyRecognizer extends EntityRecognizer {

    @Value("${properties.noCatalogFix}")
    private boolean noCatalogFix;

    @Autowired
    private AdditionalPropertyIndicatorsProvider indicatorsProvider;

    public OntologyPropertyRecognizer(@Value("${es.property_index}") String indexName,
                                      @Value("${data.ontology}") String ontologyFilePath,
                                      @Value("${properties.languages}") String[] languages) {
        super(indexName, ontologyFilePath, languages, new LabeledURIJsonParser(languages));
    }

    /**
     * Annotates the question with all properties and classes that match at least one of the w-shingles.
     *
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {


        // infer ontology properties from found entities
        for (String ignored : question.getLocationEntities()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/spatial");
        }
        if (!question.getFiletypeEntities().isEmpty()) {
            for (String ignored : question.getFiletypeEntities()) {
                question.getOntologyProperties().add("http://purl.org/dc/terms/format");
            }
            question.getOntologyProperties().add("http://www.w3.org/ns/dcat#distribution");
        }
        for (String ignored : question.getLanguageEntities()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/language");
        }
        for (String ignored : question.getThemeEntities()) {
            question.getOntologyProperties().add("http://www.w3.org/ns/dcat#theme");
        }
        for (String ignored : question.getLicenseEntities()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/license");
        }

        for (String ignored : question.getFrequencyEntities()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/accrualPeriodicity");
        }

        // infer ontology properties from additional properties
        if (question.getAdditionalProperties().contains(Question.properties.ORDER_BY_BYTESIZE) ||
                indicatorsProvider.getBytesizePropertyIndicators(question.getLanguage()).stream().anyMatch(question.getQuestionStr().toLowerCase()::startsWith)) {
            Utils.addIfNotPresent(question.getOntologyProperties(), "http://www.w3.org/ns/dcat#byteSize");
        }
        if (question.getAdditionalProperties().contains(Question.properties.ORDER_BY_ISSUED)) {
            Utils.addIfNotPresent(question.getOntologyProperties(), "http://purl.org/dc/terms/issued");
        }
        // query property index with shingles
        if (question.getWShingles() != null) {
            // use lower fuzziness for english because english words are stemmed
            String fuzziness = question.getLanguage() == Language.ENGLISH ? "1" : "2";
            for (String shingle : question.getWShingles()) {
                Utils.addAllIfNotPresent(question.getOntologyProperties(), recognizeEntities(shingle, question.getLanguage(), 1, fuzziness));
            }
        }
        // if only a date is present and neither issued nor modified was recognized the meant property is probably issued
        // for example "Give me datasets from May 2018"
        if ((!question.getTimeEntities().isEmpty() || !question.getTimeIntervalEntities().isEmpty()) && !question.getOntologyProperties().contains("http://purl.org/dc/terms/modified")) {
            Utils.addIfNotPresent(question.getOntologyProperties(), "http://purl.org/dc/terms/issued");
        }

        return question;
    }


}
