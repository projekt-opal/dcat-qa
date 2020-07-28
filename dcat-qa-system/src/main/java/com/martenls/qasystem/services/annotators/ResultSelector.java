package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Answer;
import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.SPARQLService;
import com.martenls.qasystem.utils.SPARQLUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultSelector implements QuestionAnnotator {


    @Override
    public Question annotate(Question question) {
        if (question.getAdditionalProperties().contains(Question.properties.ASK_QUERY)) {
            List<Query> resultCanditates;
            resultCanditates = question.getQueryCandidates()
                    .stream()
                    .filter(x -> x.getAskResult() != null)
                    .collect(Collectors.toList());

            if (!question.getQueryCandidates().isEmpty() && !resultCanditates.isEmpty()) {
                question.setAnswer(new Answer("{ \"boolean\": " + resultCanditates.stream().anyMatch(Query::getAskResult) + " }", resultCanditates.get(0).getQueryStr()));
            }
        } else {
            List<Query> resultCanditates;
            // discard query with empty result set
            resultCanditates = question.getQueryCandidates()
                    .stream()
                    .filter(x -> x.getSelectResult() != null)
                    .filter(x -> x.getSelectResult().hasNext())
                    .collect(Collectors.toList());

            // sort descending if count query
            if (question.getAdditionalProperties().contains(Question.properties.COUNT)) {
                resultCanditates.sort((x, y) -> {
                    if (x.getTemplate().hasCountAggregate() && y.getTemplate().hasCountAggregate()) {
                        if (SPARQLUtils.getCountFromRS(x.getSelectResult()) > SPARQLUtils.getCountFromRS(y.getSelectResult())) {
                            return -1;
                        } else if (SPARQLUtils.getCountFromRS(x.getSelectResult()) < SPARQLUtils.getCountFromRS(y.getSelectResult())) {
                            return 1;
                        }
                    }
                    return 0;
                });
            }
            // set first candidate as answer
            if (!question.getQueryCandidates().isEmpty() && !resultCanditates.isEmpty()) {
                question.setAnswer(new Answer(SPARQLService.resultSetToString(resultCanditates.get(0).getSelectResult()), resultCanditates.get(0).getQueryStr()));
            }
        }
        return question;
    }
}
