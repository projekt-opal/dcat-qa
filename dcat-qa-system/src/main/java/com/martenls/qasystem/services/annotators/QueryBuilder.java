package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import com.martenls.qasystem.models.TemplateRated;
import com.martenls.qasystem.utils.Combinatorics;

import com.martenls.qasystem.utils.Utils;
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

    /**
     * Builds all possible queries for one pair of a template and an annotated question
     * by filling the template with every combination and permutation of properties, entities and string literals
     * the question was annotated with.
     *
     * @param template which should be filled to build a query
     * @param question question the query should answer which is annotated with properties, etc. that the template should be filled with.
     * @return list of valid queries
     */
    public List<Query> buildQueriesfromTemplateQuestionPairs(Template template, Question question) {
        List<Query> queries = new ArrayList<>();
        List<String> queryStrings = new ArrayList<>();

        // create queryStrings with all possible combinations and permutations of filling in properties
        if (!question.getOntologyProperties().isEmpty() && template.getPropertyCount() > 0) {
            List<List<String>> propertyCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(new ArrayList<>(question.getOntologyProperties()), template.getPropertyCount());
            for (List<String> propertyCombination : propertyCombinations) {
                String queryStr = template.getTemplateStr();
                for (int i = 0; i < template.getPropertyCount(); i++) {
                    queryStr = queryStr.replaceAll("<prop" + i + ">", "<" + propertyCombination.get(i) + ">");
                }
                queryStrings.add(queryStr);
            }
        }


        // create queryStrings with all possible combinations and permutations of filling in entities
        if (!question.getEntities().isEmpty() && template.getEntityCount() > 0) {
            List<String> queryStringsWithEntities = new ArrayList<>();
            List<List<String>> entityCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(question.getEntities(), template.getEntityCount());

            for (String queryString : queryStrings) {
                for (List<String> entityCombination : entityCombinations) {
                    for (int i = 0; i < template.getEntityCount(); i++) {
                        queryStringsWithEntities.add(queryString.replaceAll("<entity" + i + ">", "<" + entityCombination.get(i) + ">"));
                    }
                }
            }
            queryStrings = queryStringsWithEntities;
        }

        // create queryStings with all possible combinations and permutations of filling in stringLiterals
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


        if ( !question.getTimeIntervalEntities().isEmpty() && template.hasIntervalFilter()) {
            List<String> queryStringsWithIntervalEntities = new ArrayList<>();
            for (String queryString : queryStrings) {
                queryString = queryString.replaceAll("<lbound0>", Utils.calendarToXsdDate(question.getTimeIntervalEntities().get(0).first));
                queryStringsWithIntervalEntities.add(queryString.replaceAll("<rbound0>", Utils.calendarToXsdDate(question.getTimeIntervalEntities().get(0).second)));
            }
            queryStrings = queryStringsWithIntervalEntities;
        }

        // create queries from all valid queryStrings
        for (String queryString : queryStrings) {
            if (!queryString.contains("<prop")
                    && !queryString.contains("<entity")
                    && !queryString.contains("<stringArray")
                    && !queryString.contains("<lbound")
                    && !queryString.contains("<rbound")
            ) {
                queries.add(new Query(template, queryString));
            }
        }

        return queries;
    }



}
