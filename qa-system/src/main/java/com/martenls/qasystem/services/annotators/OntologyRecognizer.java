package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.indexing.OntologyIndexer;
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
import java.util.stream.Collectors;

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
     * Connects to ESIndex when created. Retries connection every 2 seconds if it fails.
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(propertyIndex) || !searchService.checkIndexExistence(classIndex)) {
                OntologyRDFParser parser = new OntologyRDFParser();
                parser.parse(ontologyFilePath);
                OntologyIndexer indexer = new OntologyIndexer(searchService, propertyIndex, classIndex);
                if (!searchService.checkIndexExistence(propertyIndex)) {
                    indexer.indexProperties(parser.getParsedProperties());
                }
                if (!searchService.checkIndexExistence(classIndex)) {
                    indexer.indexClasses(parser.getParsedClasses());
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
                question.getOntologyClasses().addAll(recognizeDcatClasses(shingle, question.getLanguage()));
            }
        }

        if (!question.getLocations().isEmpty()) {
            question.getOntologyProperties().add("http://purl.org/dc/terms/spatial");
        }

        // TODO: more inference rules like this

        // temporal entities -> dcat:issued/dct:modified

        // language entities -> dct:language

        return question;
    }

    /**
     * Queries the property index for the given word in the given language.
     * @param word to query
     * @param language to query in
     * @return list of matched properties
     */
    private List<String> recognizeDcatProperties(String word, String language) {
        try {
            return searchService.queryIndex("label_" + language, word, propertyIndex, 10, "1").stream()
                    .map(x -> x.get("uri"))
                    .collect(Collectors.toList());
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
    private List<String> recognizeDcatClasses(String word, String language) {
        try {
            return searchService.queryIndex("label_" + language, word, classIndex, 10, "1").stream()
                    .map(x -> x.get("uri"))
                    .collect(Collectors.toList());
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat classes: ESIndex not available");
            return Collections.emptyList();
        }
    }


}
