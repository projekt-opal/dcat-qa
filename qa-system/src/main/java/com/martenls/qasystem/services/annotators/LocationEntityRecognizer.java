package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexing.LaunutsIndexer;
import com.martenls.qasystem.indexing.LaunutsRDFParser;
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
import java.util.stream.Collectors;

@Log4j2
@Service
public class LocationEntityRecognizer implements QuestionAnnotator{

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.launuts_index}")
    private String locationIndex;

    @Value("${launuts.file}")
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
                question.getLocations().addAll(recognizeLocations(shingle));
            }
        }
        return question;
    }


    private List<String> recognizeLocations(String word) {
        List<String> results = new ArrayList<>();
        try {
            results.addAll(searchService.queryIndex("prefLabel", word, locationIndex, 10, "0").stream()
                    .map(x -> x.get("uri"))
                    .collect(Collectors.toList())
            );
            results.addAll(searchService.queryIndex("altLabel", word, locationIndex, 10, "0").stream()
                    .map(x -> x.get("uri"))
                    .collect(Collectors.toList())
            );
            return results;
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch dcat properties: ESIndex not available");
            return Collections.emptyList();
        }
    }

}
