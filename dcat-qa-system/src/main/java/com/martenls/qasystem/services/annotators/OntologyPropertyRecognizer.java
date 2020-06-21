package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.indexers.LabeledURIIndexer;
import com.martenls.qasystem.parsers.OntologyJsonParser;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Enables the recognition of dcat properties and classes among words.
 */
@Log4j2
@Service
public class OntologyPropertyRecognizer implements QuestionAnnotator {

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.property_index}")
    private String propertyIndex;

    @Value("${ontology}")
    private String ontologyFilePath;

    @Value("${properties.languages}")
    private String[] languages;

    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present  .
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(propertyIndex)) {
                OntologyJsonParser parser = new OntologyJsonParser(languages);
                parser.parse(ontologyFilePath);
                LabeledURIIndexer indexer = new LabeledURIIndexer(searchService, propertyIndex, languages);
                if (!searchService.checkIndexExistence(propertyIndex)) {
                    indexer.indexEntities(parser.getParsedEntities());
                }

            } else {
                log.debug("Both class-index and property-index present, nothing to be done");
            }
        } catch (ESIndexUnavailableException e) {
            log.error("Could not init indices: ESIndex not available");
        }

    }

    /**
     * Annotates the question with all properties and classes that match at least one of the w-shingles.
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getOntologyProperties().addAll(recognizeDcatProperties(shingle, question.getLanguage()));
                //question.getOntologyClasses().addAll(recognizeDcatClasses(shingle, question.getLanguage()));
            }
        }
        // infer ontology properties from found entities
        if (!question.getLocationEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/spatial");
        }
        if (!question.getFileFormatEntities().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/format");
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
        // TODO: more inference rules like this

        // temporal entities -> dcat:issued/dct:modified


        return question;
    }

    /**
     * Queries the property index for the given word in the given language.
     * @param word to query
     * @param language to query in
     * @return list of matched properties
     */
    private List<String> recognizeDcatProperties(String word, Language language) {
        try {
            return searchService.queryIndexForLabeledUri("labels" + language.getIsoCode639_1().toString().toUpperCase(), word, propertyIndex, 1, "2");
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat properties: ESIndex not available");
            return Collections.emptyList();
        }
    }



}
