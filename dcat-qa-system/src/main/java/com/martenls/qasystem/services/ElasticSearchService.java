package com.martenls.qasystem.services;

import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Log4j2
@Service
public class ElasticSearchService {

    private final RestHighLevelClient elasticSearchClient;

    private final SearchSourceBuilder searchSourceBuilder;

    BulkProcessor bulkProcessor;

    public ElasticSearchService(@Qualifier("elasticsearchClient") RestHighLevelClient elasticSearchClient) {
        searchSourceBuilder = new SearchSourceBuilder();
        this.elasticSearchClient = elasticSearchClient;
    }

    @PostConstruct
    private void init() {
        boolean esUnavailable = true;
        while (esUnavailable) {
            try {
                elasticSearchClient.ping(RequestOptions.DEFAULT);
                esUnavailable = false;
            } catch (IOException e) {
                log.error("ElasticSearch index can not be reached. Will try again in 2 seconds... ", e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    log.error(interruptedException.getMessage());
                }
            }
        }


        BulkProcessor.Builder builder = BulkProcessor.builder(
                (req, bulkListener) ->
                        elasticSearchClient.bulkAsync(req, RequestOptions.DEFAULT, bulkListener),
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        // not needed
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        // not needed
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        // not needed
                    }
                });
        builder.setBulkActions(1000);
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(0);
        builder.setFlushInterval(TimeValue.timeValueSeconds(2L));
        bulkProcessor = builder.build();
    }

    /**
     * Query index with given name for given key and value pair.
     * @param key of key value pair that is wanted
     * @param value of key value pair that is wanted
     * @param index to query
     * @param maxNumberOfResults
     * @return
     * @throws ESIndexUnavailableException
     */
    public List<Map<String,String>> queryIndex(String key, String value, String index, int maxNumberOfResults, String fuzziness) throws ESIndexUnavailableException {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(key, value)
                .fuzziness(fuzziness)
                .fuzzyTranspositions(false)
                .maxExpansions(2);

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(maxNumberOfResults);

        SearchRequest searchRequest = new SearchRequest();

        searchRequest.source(searchSourceBuilder).indices(index);

        SearchHits hits;
        try {
            hits = elasticSearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits();
        } catch (IOException e) {
            throw new ESIndexUnavailableException();
        }

        if (hits.getHits().length == 0)
            return Collections.emptyList();

        List<Map<String,String>> results = new ArrayList<>();

        for (SearchHit hit : hits.getHits()) {
            Map<String, String> hitMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : hit.getSourceAsMap().entrySet()) {
                hitMap.put(entry.getKey(), (String) entry.getValue());
            }
            results.add(hitMap);
        }

        return results;

//        return Arrays.stream(hits.getHits())
//                .map(SearchHit::getSourceAsMap)
//                .map(x -> x.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e == null? null : (String) e.getValue())))
//                .collect(Collectors.toList());
    }

    public boolean checkIndexExistence(String index) throws ESIndexUnavailableException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            return elasticSearchClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ESIndexUnavailableException();
        }

    }


    /**
     * Sends an index creation request with the given name and mapping to the ES instance.
     * @param index name of the index that should be created
     * @param mapping with properties of the index
     * @throws ESIndexUnavailableException if ES index can not be reached
     */
    public void createIndex(String index, XContentBuilder mapping) throws ESIndexUnavailableException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(mapping);
        try {
            elasticSearchClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ESIndexUnavailableException();
        }
        log.debug("Index " + index + " was created with response");

    }

    /**
     * Adds a new IndexRequest for the given index and mapping and adds it to the bulkProcessor.
     * @param index name of the index where the mapping should be indexed
     * @param mapping that should be indexed
     */
    public void makeIndexRequest(String index, XContentBuilder mapping) {
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(mapping);
        this.bulkProcessor.add(indexRequest);
    }


    @PreDestroy
    private void cleanup() throws IOException {
        elasticSearchClient.close();
    }

}
