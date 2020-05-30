package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.SPARQLService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ResultSelector implements QuestionAnnotator {


    @Override
    public Question annotate(Question question) {

        // discard query with empty result set
        question.setQueryCandidates(
                question.getQueryCandidates()
                        .stream()
                        .filter(x -> x.getResultSet().hasNext())
                        .collect(Collectors.toList())
        );

        // TODO: compare answer type predicted from question with result

        // TODO: check if count > 0 for count queries

        if (!question.getQueryCandidates().isEmpty()) {
            question.setAnswer(SPARQLService.resultSetToString(question.getQueryCandidates().get(0).getResultSet()));
        }

        return question;
    }
}
