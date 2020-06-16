package com.martenls.qasystem.indexing;


import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.models.Location;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Log4j2
public class LaunutsIndexer {

    private final ElasticSearchService elasticSearch;

    private final String launutsIndex;

    public LaunutsIndexer(ElasticSearchService elasticSearch, String launutsIndex) {
        this.elasticSearch = elasticSearch;
        this.launutsIndex = launutsIndex;
    }


    public void indexLocations(List<Location> locations) throws ESIndexUnavailableException {
        try {
            this.createLaunutsIndex();
            for (Location location: locations) {
                XContentBuilder mapping = jsonBuilder()
                        .startObject()
                            .field("uri", location.getUri())
                            .field("prefLabel", location.getPref_label())
                            .field("altLabel", location.getAlt_label())
                        .endObject();
                elasticSearch.makeIndexRequest(launutsIndex, mapping);
            }
        }  catch (IOException e) {
            throw new ESIndexUnavailableException();
        }
        log.debug("Added " + locations.size() + " locations to the index " + launutsIndex);

    }


    private void createLaunutsIndex() throws ESIndexUnavailableException {
        try {
            XContentBuilder mapping = jsonBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("uri")
                            .   field("type", "keyword")
                            .endObject()
                            .startObject("prefLabel")
                                .field("type", "text")
                                .field("analyzer", "keyword")
                            .endObject()
                            .startObject("altLabel")
                                .field("type", "text")
                                .field("analyzer", "keyword")
                            .endObject()
                        .endObject()
                    .endObject();
            log.debug(Strings.toString(mapping));

            elasticSearch.createIndex(launutsIndex, mapping);
        } catch (IOException e) {
            log.error(e);
            throw new ESIndexUnavailableException();
        }
        log.debug("Created index: " + launutsIndex);


    }
}
