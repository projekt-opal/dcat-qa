package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.indexing.OntologyIndexer;
import com.martenls.qasystem.indexing.OntologyJsonParser;
import com.martenls.qasystem.indexing.OntologyRDFParser;
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

/**
 * Enables the recognition of dcat properties and classes among words.
 */
@Log4j2
@Service
public class OntologyRecognizer implements QuestionAnnotator{

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.property_index}")
    private String propertyIndex;

    @Value("${es.class_index}")
    private String classIndex;

    @Value("${ontology.file}")
    private String ontologyFilePath;

    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present  .
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(propertyIndex) || !searchService.checkIndexExistence(classIndex)) {
                if (ontologyFilePath.endsWith(".json")) {
                    OntologyJsonParser parser = new OntologyJsonParser();
                    parser.parse(ontologyFilePath);
                    OntologyIndexer indexer = new OntologyIndexer(searchService, propertyIndex, classIndex);
                    if (!searchService.checkIndexExistence(propertyIndex)) {
                        indexer.indexPropertiesWithSynonyms(parser.getParsedProperties());
                    }
                } else if (ontologyFilePath.endsWith(".xml")) {
                    OntologyRDFParser parser = new OntologyRDFParser();
                    parser.parse(ontologyFilePath);
                    OntologyIndexer indexer = new OntologyIndexer(searchService, propertyIndex, classIndex);
                    if (!searchService.checkIndexExistence(propertyIndex)) {
                        indexer.indexProperties(parser.getParsedProperties());
                    }
                    if (!searchService.checkIndexExistence(classIndex)) {
                        indexer.indexClasses(parser.getParsedClasses());
                    }
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
            return searchService.queryIndexForLabeledUri("label_" + language.getIsoCode639_1().toString(), word, propertyIndex, 10, "2");
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat properties: ESIndex not available");
            return Collections.emptyList();
        }
    }

    /**
     * Queries the class index for the given word in the given language.
     * @param word to query
     * @param language to query in
     * @return list of matched classes
     */
    private List<String> recognizeDcatClasses(String word, Language language) {
        try {
            return searchService.queryIndexForLabeledUri("label_" + language.getIsoCode639_1().toString(), word, classIndex, 10, "1");
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat classes: ESIndex not available");
            return Collections.emptyList();
        }
    }


}
