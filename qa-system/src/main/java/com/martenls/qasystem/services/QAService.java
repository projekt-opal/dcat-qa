package com.martenls.qasystem.services;

import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.annotators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

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
    private TemplateSelector templateSelector;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private SPARQLService sparqlService;

    @Autowired
    private ResultSelector resultSelector;



    public Question answerQuestion(Question question) {

        languageRecognizer.annotate(question);
        semanticAnalyzer.annotate(question);
        locationEntityRecognizer.annotate(question);
        ontologyRecognizer.annotate(question);
        templateSelector.annotate(question);
        queryBuilder.annotate(question);

        for (Query queryCandidate : question.getQueryCandidates()) {
            queryCandidate.setResultSet(sparqlService.executeQueryRS(queryCandidate.getQueryStr()));
        }

        resultSelector.annotate(question);



        return question;
    }

}
