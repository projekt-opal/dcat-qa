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

        // create queryStrings with all possible permutations of filling in properties
        List<List<String>> propertyCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(new ArrayList<>(question.getOntologyProperties()), template.getPropertyCount());
        for (List<String> propertyCombination : propertyCombinations) {
            String queryStr = template.getTemplateStr();
            for (int i = 0; i < template.getPropertyCount(); i++) {
                queryStr = queryStr.replaceAll("<prop" + i + ">", "<" + propertyCombination.get(i) + ">");
            }
            queryStrings.add(queryStr);
        }

        // create queryStrings with all possible permutations of filling in entities
        if (!question.getLocationEntities().isEmpty() && template.getEntityCount() > 0) {
            List<String> queryStringsWithEntities = new ArrayList<>();
            List<List<String>> entityCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(new ArrayList<>(question.getLocationEntities()), template.getEntityCount());

            for (String queryString : queryStrings) {
                for (List<String> entityCombination : entityCombinations) {
                    for (int i = 0; i < template.getEntityCount(); i++) {
                        queryStringsWithEntities.add(queryString.replaceAll("<entity" + i + ">", "<" + entityCombination.get(i) + ">"));
                    }

                }
            }
            queryStrings = queryStringsWithEntities;
        }

        // create queryStings with all possible permutations of filling in stringLiterals
        if (!question.getStringLiterals().isEmpty() && template.getStringArrayCount() > 0) {
            List<String> queryStringsWithStringEntities = new ArrayList<>();
            List<List<String>> stringCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(question.getStringLiterals(), template.getStringArrayCount());

            for (String queryString : queryStrings) {
                for (List<String> stringCombination : stringCombinations) {
                    for (int i = 0; i < template.getStringArrayCount(); i++) {
                        queryStringsWithStringEntities.add(queryString.replaceAll("<stringArray" + i + ">", "\"" + stringCombination.get(i) + "\""));
                    }

                }
            }
            queryStrings = queryStringsWithStringEntities;
        }

        // create queries from queryStrings
        for (String queryString : queryStrings) {
            queries.add(new Query(template, queryString));
        }


        return queries;
    }



}
