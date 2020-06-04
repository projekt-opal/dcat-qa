package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.SPARQLService;
import com.martenls.qasystem.utlis.SPARQLUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultSelector implements QuestionAnnotator {


    @Override
    public Question annotate(Question question) {

        List<Query> resultCanditates;
        // discard query with empty result set
        resultCanditates = question.getQueryCandidates()
                .stream()
                .filter(x -> x.getResultSet().hasNext())
                .collect(Collectors.toList());



        // TODO: compare answer type predicted from question with result

        // TODO: check if count > 0 for count queries

        resultCanditates = resultCanditates.stream().filter(x -> {
            if (x.getTemplate().hasCountAggregate())
                return SPARQLUtils.getCountFromRS(x.getResultSet()) > 0;
            else
                return true;
        }).collect(Collectors.toList());


        if (!question.getQueryCandidates().isEmpty()) {
            question.setAnswer(SPARQLService.resultSetToString(resultCanditates.get(0).getResultSet()));
        }

        return question;
    }
}
