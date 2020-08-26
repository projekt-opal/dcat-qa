package com.martenls.qasystem.indexers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.models.LabeledURI;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class LabeledURIIndexer {


    private final ElasticSearchService elasticSearch;

    /**
     * Name of the index.
     */
    private final String indexName;

    private final Set<String> languages;

    public LabeledURIIndexer(ElasticSearchService elasticSearch, String indexName, String[] languages) {
        this.elasticSearch = elasticSearch;
        this.indexName = indexName;
        this.languages = Set.of(languages);

    }

    /**
     * Creates a new index for languages and sends index requests to the elastic search index for all
     * given languages.
     *
     * @param labeledEntities that should be indexed
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    public void indexEntities(List<LabeledURI> labeledEntities) throws ESIndexUnavailableException {
        this.createIndex(indexName);
        for (LabeledURI labeledUri : labeledEntities) {
            this.indexEntity(labeledUri.getWithLowercasedLabels(), indexName);
        }

        log.debug("Added " + labeledEntities.size() + " entities to the index " + indexName);
    }

    /**
     * Creates index with the given name and the ontology specific properties.
     *
     * @param index name of the index
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    private void createIndex(String index) throws ESIndexUnavailableException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mapping = mapper.createObjectNode();
        ObjectNode properties = mapper.createObjectNode();
        ObjectNode uri = mapper.createObjectNode();
        uri.put("type", "keyword");
        properties.set("uri", uri);

        for (String language : languages) {
            ObjectNode property = mapper.createObjectNode();
            property.put("type", "text");
            property.put("analyzer", "keyword");
            properties.set("labels" + language.toUpperCase(), property);
        }
        mapping.set("properties", properties);

        elasticSearch.createIndex(index, mapping.toString());

        log.debug("Created index: " + index);

    }

    /**
     * Builds an IndexRequest for a DCAT property or class according to the properties set in createIndex().
     *
     * @param labeledUri DCAT property or class
     * @param index      name of the index
     * @return IndexRequest for the DCAT property or class
     */
    private void indexEntity(LabeledURI labeledUri, String index) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mapping = mapper.createObjectNode();
        mapping.put("uri", labeledUri.getUri());
        for (Map.Entry<String, List<String>> labelsByLanguage : labeledUri.getLabels().entrySet()) {
            mapping.set("labels" + labelsByLanguage.getKey().toUpperCase(), mapper.valueToTree(labelsByLanguage.getValue()));
        }

        elasticSearch.makeIndexRequest(index, mapping.toString());
    }
}


