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
    private OntologyPropertyRecognizer ontologyPropertyRecognizer;

    @Autowired
    private LocationEntityRecognizer locationEntityRecognizer;

    @Autowired
    private LanguageEntityRecognizer languageEntityRecognizer;

    @Autowired
    private ThemeEntityRecognizer themeEntityRecognizer;

    @Autowired
    private LicenseEntityRecognizer licenseEntityRecognizer;

    @Autowired
    private TimeEntityRecognizer timeEntityRecognizer;

    @Autowired
    private FrequencyEntityRecognizer frequencyEntityRecognizer;

    @Autowired
    private FiletypeEntityRecognizer filetypeEntityRecognizer;

    @Autowired
    private NLPAnnotator nlpAnnotator;

    @Autowired
    private TemplateSelector templateSelector;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private SPARQLService sparqlService;

    @Autowired
    private ResultSelector resultSelector;


    public Question answerQuestion(Question question) {
        return answerQuestion(question, 0);
    }

    public Question answerQuestion(Question question, int resultLimit) {
        try {
            languageRecognizer.annotate(question);
            nlpAnnotator.annotate(question);
            semanticAnalyzer.annotate(question);

            // Entity Recognition
            timeEntityRecognizer.annotate(question);
            locationEntityRecognizer.annotate(question);
            languageEntityRecognizer.annotate(question);
            themeEntityRecognizer.annotate(question);
            licenseEntityRecognizer.annotate(question);
            frequencyEntityRecognizer.annotate(question);
            filetypeEntityRecognizer.annotate(question);

            // Property recognition and inference from entities
            ontologyPropertyRecognizer.annotate(question);

            templateSelector.annotate(question);
            queryBuilder.annotate(question);

            if (question.getAdditionalProperties().contains(Question.properties.ASK_QUERY)) {
                for (Query queryCandidate : question.getQueryCandidates()) {
                    queryCandidate.setAskResult(sparqlService.executeAskQuery(queryCandidate.getQueryStr()));
                }
            } else {
                for (Query queryCandidate : question.getQueryCandidates()) {
                    queryCandidate.setSelectResult(sparqlService.executeSelectQuery(queryCandidate.getQueryStr(), resultLimit));
                }
            }

            resultSelector.annotate(question);
        } catch (Exception e) {
            log.error(question.toString(), e);
        }

        return question;
    }

}
