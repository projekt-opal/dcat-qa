package com.martenls.qasystem.indexing;


import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.models.LabeledURI;
import com.martenls.qasystem.models.DcatClass;
import com.martenls.qasystem.models.DcatProperty;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Enables indexing of the properties and classes of the DCAT ontology.
 */
@Log4j2
public class OntologyIndexer {


    private final ElasticSearchService elasticSearch;
    /**
     * Name of the property index.
     */
    private final String propertyIndex;
    /**
     * Name of the class index.
     */
    private final String classIndex;

    public OntologyIndexer(ElasticSearchService elasticSearch, String propertyIndex, String classIndex) {
        this.elasticSearch = elasticSearch;
        this.propertyIndex = propertyIndex;
        this.classIndex = classIndex;
    }

    /**
     * Creates a new index for DCAT properties and sends index requests to the elastic search index for all
     * given properties.
     * @param properties that should be indexed
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    public void indexProperties(List<DcatProperty> properties) throws ESIndexUnavailableException {
        try {
            this.createDcatIndex(propertyIndex);
            for (DcatProperty property : properties) {
                this.indexDcatElement(property, propertyIndex);
            }
        }  catch (IOException e) {
            throw new ESIndexUnavailableException();
        }
        log.debug("Added " + properties.size() + " properties to the index " + propertyIndex);
    }

    /**
     * Creates a new index for DCAT classes and sends index requests to the elastic search index for all
     * given classes.
     * @param classes that should be indexed
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    public void indexClasses(List<DcatClass> classes) throws ESIndexUnavailableException {
        try {
            this.createDcatIndex(classIndex);
            for (DcatClass dcatClass: classes) {
                this.indexDcatElement(dcatClass, classIndex);
            }
        } catch (IOException e) {
            throw new ESIndexUnavailableException();
        }
        log.debug("Added " + classes.size() + " classes to the index " + classIndex);
    }

    /**
     * Creates index with the given name and the ontology specific properties.
     * @param index name of the index
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    private void createDcatIndex(String index) throws ESIndexUnavailableException {
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
    private void indexDcatElement(LabeledURI labeledURI, String index) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                    .startObject()
                        .field("uri", labeledURI.getUri())
                        .field("label_en", labeledURI.getLabel_en())
                        .field("label_de", labeledURI.getLabel_de())
                    .endObject();
        elasticSearch.makeIndexRequest(index, mapping);
    }


}
