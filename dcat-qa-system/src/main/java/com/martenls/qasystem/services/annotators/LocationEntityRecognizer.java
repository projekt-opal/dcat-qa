package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexers.LaunutsIndexer;
import com.martenls.qasystem.parsers.LaunutsRDFParser;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class LocationEntityRecognizer implements QuestionAnnotator{

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.launuts_index}")
    private String locationIndex;

    @Value("${launuts}")
    private String launutsFilePath;


    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(locationIndex)) {
                LaunutsRDFParser parser = new LaunutsRDFParser();
                parser.parse(launutsFilePath);
                LaunutsIndexer indexer = new LaunutsIndexer(searchService, locationIndex);
                indexer.indexLocations(parser.getParsedLocations());
            } else {
                log.debug("Launuts index is present, nothing to be done");
            }
        } catch (ESIndexUnavailableException e) {
            log.error("Could not init indices: ESIndex not available");
        }
    }

    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getLocationEntities().addAll(recognizeLocations(shingle));
            }
        }
        return question;
    }


    private List<String> recognizeLocations(String word) {
        List<String> results = new ArrayList<>();
        try {
            results.addAll(searchService.queryIndexForLabeledUri("prefLabel", word, locationIndex, 10, "0"));
            results.addAll(searchService.queryIndexForLabeledUri("altLabel", word, locationIndex, 10, "0"));
            return results;
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat properties: ESIndex not available");
            return Collections.emptyList();
        }
    }

}
