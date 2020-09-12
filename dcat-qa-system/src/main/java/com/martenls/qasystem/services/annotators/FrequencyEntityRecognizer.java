package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.LabeledURIJsonParser;
import com.martenls.qasystem.services.ElasticSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FrequencyEntityRecognizer extends EntityRecognizer {


    public FrequencyEntityRecognizer(ElasticSearchService searchService,
                                     @Value("${es.frequency_index}") String indexName,
                                     @Value("${data.frequencyEntities}") String rdfFilePath,
                                     @Value("${properties.languages}") String[] languages
    ) {
        super(indexName, rdfFilePath, languages, new LabeledURIJsonParser(languages));
    }


    /**
     * Annotates the question with all frequency entities that match at least one of the w-shingles.
     *
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getFrequencyEntities().addAll(recognizeEntities(shingle, question.getLanguage(), 1, "1"));
            }
        }
        return question;
    }


}
