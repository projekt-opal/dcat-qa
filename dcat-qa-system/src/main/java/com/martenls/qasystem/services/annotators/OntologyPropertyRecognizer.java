package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.config.SemanticPropertyIndicators;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.LabeledURIJsonParser;
import lombok.extern.log4j.Log4j2;
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
        if (question.getWShingles() != null) {
            // use lower fuzziness for english because english words are stemmed
            String fuzziness = question.getLanguage() == Language.ENGLISH ? "1" : "2";
            for (String shingle : question.getWShingles()) {
                question.getOntologyProperties().addAll(recognizeEntities(shingle, question.getLanguage(), 1, fuzziness));
            }
        }
        if (question.getWShinglesWithStopwords().stream().anyMatch(SemanticPropertyIndicators.getBytesizePropetyIndicators(question.getLanguage())::contains)) {
            question.getOntologyProperties().add("http://www.w3.org/ns/dcat#byteSize");
        }

        // infer ontology properties from found entities
        if (!question.getLocationEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/spatial");
        }
        if (!question.getFiletypeEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/format");
            question.getOntologyProperties().add("http://www.w3.org/ns/dcat#distribution");
        }
        if (!question.getLanguageEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/language");
        }
        if (!question.getThemeEntities().isEmpty()) {
            question.getOntologyProperties().add("http://www.w3.org/ns/dcat#theme");
        }
        if (!question.getLicenseEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/license");
        }
        if (!question.getTimeEntities().isEmpty() || !question.getTimeIntervalEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/issued");
        }
        if (!question.getFrequencyEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/accrualPeriodicity");
        }
        // TODO: more inference rules like this

        // temporal entities -> dcat:issued/dct:modified

        // Fix for absence of dcat:Catalogs in opal2020-07 dataset
        if (noCatalogFix && question.getOntologyProperties().contains("http://www.w3.org/ns/dcat#dataset")) {
            question.getOntologyProperties().remove("http://www.w3.org/ns/dcat#dataset");
            question.getOntologyProperties().add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            question.getOntologyClasses().add("http://www.w3.org/ns/dcat#Dataset");
        }

        return question;
    }


}
