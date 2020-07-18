package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
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
public class LocationEntityRecognizer extends EntityRecognizer {


    public LocationEntityRecognizer(@Value("${es.launuts_index}") String indexName,
                                    @Value("${data.launuts}") String rdfFilePath,
                                    @Value("${properties.languages}") String[] languages) {
        super(indexName, rdfFilePath, languages, new LaunutsRDFParser());
    }


    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getLocationEntities().addAll(recognizeEntities(shingle, Language.GERMAN, 10, "0"));
            }
        }
        return question;
    }



}
