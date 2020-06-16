package com.martenls.qasystem.services;

import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.annotators.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class QAService {

    @Autowired
    private LanguageRecognizer languageRecognizer;

    @Autowired
    private SemanticAnalyzer semanticAnalyzer;

    @Autowired
    private OntologyRecognizer ontologyRecognizer;

    @Autowired
    private LocationEntityRecognizer locationEntityRecognizer;

    @Autowired
    private LanguageEntityRecognizer languageEntityRecognizer;

    @Autowired
    private TimeEntityRecognizer timeEntityRecognizer;

    @Autowired
    private TemplateSelector templateSelector;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private SPARQLService sparqlService;

    @Autowired
    private ResultSelector resultSelector;



    public Question answerQuestion(Question question) {
        try {
            languageRecognizer.annotate(question);
            semanticAnalyzer.annotate(question);
            timeEntityRecognizer.annotate(question);
            locationEntityRecognizer.annotate(question);
            languageEntityRecognizer.annotate(question);
            ontologyRecognizer.annotate(question);
            templateSelector.annotate(question);
            queryBuilder.annotate(question);

            for (Query queryCandidate : question.getQueryCandidates()) {
                queryCandidate.setResultSet(sparqlService.executeQuery(queryCandidate.getQueryStr()));
            }

            resultSelector.annotate(question);
        } catch(Exception e) {
            log.error(question.toString(), e);
        }



        return question;
    }

}
