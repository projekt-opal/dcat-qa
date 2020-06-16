package com.martenls.qasystem.indexing;

import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.models.LabeledURI;
import com.martenls.qasystem.models.LanguageEntity;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


@Log4j2
public class LanguageIndexer {

    private final ElasticSearchService elasticSearch;
    /**
     * Name of the property index.
     */
    private final String languageIndex;


    public LanguageIndexer(ElasticSearchService elasticSearch, String languageIndex) {
        this.elasticSearch = elasticSearch;
        this.languageIndex = languageIndex;

    }

    /**
     * Creates a new index for languages and sends index requests to the elastic search index for all
     * given languages.
     * @param languages that should be indexed
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    public void indexLanguages(List<LanguageEntity> languages) throws ESIndexUnavailableException {
        try {
            this.createLanguageIndex(languageIndex);
            for (LanguageEntity property : languages) {
                this.indexLanguage(property, languageIndex);
            }
        }  catch (IOException e) {
            throw new ESIndexUnavailableException();
        }
        log.debug("Added " + languages.size() + " languages to the index " + languageIndex);
    }

    /**
     * Creates index with the given name and the ontology specific properties.
     * @param index name of the index
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    private void createLanguageIndex(String index) throws ESIndexUnavailableException {
        try {
            XContentBuilder mapping = jsonBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("uri")
                                .field("type", "keyword")
                            .endObject()
                            .startObject("label_en")
                                .field("type", "text")
                                .field("analyzer", "keyword")
                            .endObject()
                            .startObject("label_de")
                                .field("type", "text")
                                .field("analyzer", "keyword")
                            .endObject()
                        .endObject()
                    .endObject();
            log.debug(Strings.toString(mapping));

            elasticSearch.createIndex(index, mapping);
        } catch (IOException e) {
            log.error(e);
            throw new ESIndexUnavailableException();
        }
        log.debug("Created index: " + index);

    }

    /**
     * Builds an IndexRequest for a DCAT property or class according to the properties set in createIndex().
     * @param labeledURI DCAT property or class
     * @param index name of the index
     * @return IndexRequest for the DCAT property or class
     * @throws IOException if json object can not be build
     */
    private void indexLanguage(LabeledURI labeledURI, String index) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                    .field("uri", labeledURI.getUri())
                    .field("label_en", labeledURI.getLabel_en())
                    .field("label_de", labeledURI.getLabel_de())
                .endObject();
        elasticSearch.makeIndexRequest(index, mapping);
    }
}
