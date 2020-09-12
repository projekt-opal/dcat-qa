package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Answer;
import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.SPARQLService;
import com.martenls.qasystem.utils.SPARQLUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResultSelector implements QuestionAnnotator {


    /**
     * Selects a query and its results based on the number of results and the type of results.
     * If the question indicates an ASK query the first query with a positive result is picked. If all are negative the first is picked.
     * If the question indicates a COUNT query the query with the highest result is picked.
     * Else the first query with results is picked.
     * @param question for which a results should be selected
     * @return the question annotated with the selected query and its results
     */
    @Override
    public Question annotate(Question question) {
        if (question.getAdditionalProperties().contains(Question.properties.ASK_QUERY)) {
            List<Query> resultCanditates;
            resultCanditates = question.getQueryCandidates()
                    .stream()
                    .filter(x -> x.getAskResult() != null)
                    .collect(Collectors.toList());

            if (!question.getQueryCandidates().isEmpty() && !resultCanditates.isEmpty()) {
                Optional<Query> result = resultCanditates.stream().filter(Query::getAskResult).findFirst();
                question.setAnswer(new Answer("{ \"boolean\": " + result.isPresent() + " }", result.orElseGet(() -> resultCanditates.get(0)).getQueryStr()));
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
