package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexers.LabeledURIIndexer;
import com.martenls.qasystem.parsers.EntityRDFParser;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Log4j2
public abstract class EntityRecognizer implements QuestionAnnotator {

    @Autowired
    protected ElasticSearchService searchService;

    protected String[] languages;
    protected String indexName;
    protected String rdfFilePath;
    protected EntityRDFParser parser;

    public EntityRecognizer(String indexName, String rdfFilePath, String[] languages, EntityRDFParser parser) {
        this.indexName = indexName;
        this.rdfFilePath = rdfFilePath;
        this.languages = languages;
        this.parser = parser;
    }


    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present.
     */
    @PostConstruct
    protected void initIndices() {
        try {
            if (!searchService.checkIndexExistence(indexName)) {
                parser.parse(rdfFilePath);
                LabeledURIIndexer indexer = new LabeledURIIndexer(searchService, indexName, languages);
                if (!searchService.checkIndexExistence(indexName)) {
                    indexer.indexEntities(parser.getParsedEntities());
                }

            } else {
                log.debug("{}-index present, nothing to be done", indexName);
            }
        } catch (ESIndexUnavailableException e) {
            log.error("Could not init indices: ESIndex not available");
        }

    }


    /**
     * Queries the language index for the given word in the given language.
     *
     * @param word     to query
     * @param language to query in
     * @return list of matched properties
     */
    protected List<String> recognizeEntities(String word, Language language, int maxNumberOfResults, String fuzziness) {
        try {
            return searchService.queryIndexForLabeledUri("labels" + language.getIsoCode639_1().toString().toUpperCase(), word.toLowerCase(), indexName, maxNumberOfResults, fuzziness);
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch entities: ESIndex not available");
            return Collections.emptyList();
        }
    }


}
