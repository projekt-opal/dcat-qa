package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import com.martenls.qasystem.models.TemplateRated;
import com.martenls.qasystem.utlis.Combinatorics;

import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Fills templates with properties, entities, etc. and therefore builds valid queries.
 */
@Service
public class QueryBuilder implements QuestionAnnotator{


    @Override
    public Question annotate(Question question) {
        for (TemplateRated templateCandidate : question.getTemplateCandidates()) {
            question.getQueryCandidates().addAll(buildQueriesfromTemplateQuestionPairs(templateCandidate.getTemplate(), question));
        }
        return question;
    }


    public List<Query> buildQueriesfromTemplateQuestionPairs(Template template, Question question) {
        List<Query> queries = new ArrayList<>();

        List<String> queryStrings = new ArrayList<>();
        // get all property combinations and permutations
        List<List<String>> propertyCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(new ArrayList<>(question.getOntologyProperties()), template.getPropertyCount());

        for (List<String> propertyCombination : propertyCombinations) {
            String queryStr = template.getTemplateStr();
            for (int i = 0; i < template.getPropertyCount(); i++) {
                queryStr = queryStr.replaceAll("<prop" + i + ">", "<" + propertyCombination.get(i) + ">");
            }
            queryStrings.add(queryStr);
        }

        List<List<String>> entityCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(new ArrayList<>(question.getLocations()), template.getEntityCount());

        for (String queryString : queryStrings) {
            for (List<String> entityCombination : entityCombinations) {
                for (int i = 0; i < template.getEntityCount(); i++) {
                    queries.add(new Query(template, queryString.replaceAll("<entity" + i + ">", "<" + entityCombination.get(i) + ">")));
                }

            }
        }
        return queries;
    }



}
