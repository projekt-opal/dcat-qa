package com.martenls.qasystem.services;

import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.annotators.LanguageRecognizer;
import com.martenls.qasystem.services.annotators.LocationRecognizer;
import com.martenls.qasystem.services.annotators.OntologyRecognizer;
import com.martenls.qasystem.services.annotators.SemanticAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class QAService {

    @Autowired
    private LanguageRecognizer languageRecognizer;

    @Autowired
    private SemanticAnalysis semanticAnalyzer;

    @Autowired
    private OntologyRecognizer ontologyRecognizer;

    @Autowired
    private LocationRecognizer locationRecognizer;

    private List<String> answers;


    public Question answerQuestion(Question question) {

        languageRecognizer.annotate(question);
        semanticAnalyzer.annotate(question);
        locationRecognizer.annotate(question);
        ontologyRecognizer.annotate(question);

        return question;
    }

}
