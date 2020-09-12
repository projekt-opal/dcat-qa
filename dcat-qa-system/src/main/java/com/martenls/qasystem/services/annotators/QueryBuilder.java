package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Query;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import com.martenls.qasystem.models.TemplateRated;
import com.martenls.qasystem.utils.Combinatorics;
import com.martenls.qasystem.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Fills templates with properties, entities, etc. and therefore builds valid queries.
 */
@Service
public class QueryBuilder implements QuestionAnnotator {


    /**
     * Builds queries with the top rated templates and adds them to the question object.
     * @param question for which queries should be build
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        // consider all templates with top rating but a maximum of 5
        int numberOfConsideredTemplates = Math.min(5, (int) question.getTemplateCandidates().stream().filter(t -> t.getRating() == question.getTemplateCandidates().get(0).getRating()).count());
        for (TemplateRated templateCandidate : question.getTemplateCandidates().subList(0, numberOfConsideredTemplates)) {
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

            for (String queryStr : queryStrings) {
                combinationLoop:
                for (List<String> entityCombination : entityCombinations) {
                    for (int i = 0; i < template.getEntityCount(); i++) {
                        if (isEntityPlacementValid(question, queryStr, entityCombination.get(i), i)) {
                            queryStr = queryStr.replaceAll("<entity" + i + ">", "<" + entityCombination.get(i) + ">");
                        } else {
                            continue combinationLoop;
                        }
                    }
                    queryStringsWithEntities.add(queryStr);
                }
            }
            queryStrings = queryStringsWithEntities;
        }

        // create queryStings with all possible combinations and permutations of filling in stringLiterals
        if (!question.getLiterals().isEmpty() && template.getLiteralArrayCount() > 0) {
            List<String> queryStringsWithStringEntities = new ArrayList<>();
            List<List<String>> stringCombinations = Combinatorics.getAllCombsAndPermsOfKListElements(question.getLiterals(), template.getLiteralArrayCount());

            for (String queryString : queryStrings) {
                for (List<String> stringCombination : stringCombinations) {
                    for (int i = 0; i < template.getLiteralArrayCount(); i++) {
                        queryStringsWithStringEntities.add(queryString.replaceAll("<literalArray" + i + ">", stringCombination.get(i)));
                    }
                }
            }
            queryStrings = queryStringsWithStringEntities;
        }


        if (!question.getTimeIntervalEntities().isEmpty() && template.hasIntervalFilter()) {
            List<String> queryStringsWithIntervalEntities = new ArrayList<>();
            for (String queryString : queryStrings) {
                queryString = queryString.replaceAll("<lbound0>", Utils.calendarToXsdDate(question.getTimeIntervalEntities().get(0).first));
                queryStringsWithIntervalEntities.add(queryString.replaceAll("<rbound0>", Utils.calendarToXsdDate(question.getTimeIntervalEntities().get(0).second)));
            }
            queryStrings = queryStringsWithIntervalEntities;
        }

        List<String> queriesToBePruned = new ArrayList<>();

        // prune queries ordered by wrong property
        if ((template.hasOrderAscModifier() || template.hasOrderDescModifier())
                && (question.getAdditionalProperties().contains(Question.properties.ORDER_BY_BYTESIZE) || question.getAdditionalProperties().contains(Question.properties.ORDER_BY_ISSUED))) {
            Pattern pattern;
            if (question.getAdditionalProperties().contains(Question.properties.ORDER_BY_BYTESIZE)) {
                pattern = Pattern.compile("<http://www.w3.org/ns/dcat#byteSize>\\s\\?var(\\d)");
            } else {
                pattern = Pattern.compile("<http://purl.org/dc/terms/issued>\\s\\?var(\\d)");
            }
            for (String queryString : queryStrings) {
                Matcher matcher = pattern.matcher(queryString);
                if (matcher.find() && !Pattern.compile("ORDER BY (ASC|DESC)\\(\\?var" + matcher.group(1) + "\\)").matcher(queryString).find()) {
                    queriesToBePruned.add(queryString);
                }
            }
        }
        queryStrings.removeAll(queriesToBePruned);


        // questions for singular superlatives e.g. "What is the biggest...?" -> LIMIT 1 in querystring
        // questions for plurar superlatives e.g. "What are the biggest...?" -> LIMIT 10 in querystring
        List<String> queryStringsWithLimits = new ArrayList<>();
        for (String queryString : queryStrings) {
            if (!queryString.toLowerCase().contains("limit")
                    && (question.getAdditionalProperties().contains(Question.properties.DESC_ORDERED)
                    || question.getAdditionalProperties().contains(Question.properties.ASC_ORDERED))
                    && (template.hasOrderDescModifier()
                    || template.hasOrderAscModifier())
            ) {
                int limit = 10;
                switch (question.getLanguage()) {
                    case GERMAN:
                        if (question.getWords().stream().anyMatch(x -> x.equals("ist") || x.equals("wurde"))) {
                            limit = 1;
                        }
                        break;
                    case ENGLISH:
                        if (question.getWords().stream().anyMatch(x -> x.equals("is") || x.equals("was"))) {
                            limit = 1;
                        }
                        break;
                }
                queryString += "\nLIMIT " + limit;
            }
            queryStringsWithLimits.add(queryString);
        }
        queryStrings = queryStringsWithLimits;

        queryStrings = queryStrings.stream().distinct().collect(Collectors.toList());

        // create queries from all valid queryStrings
        for (String queryString : queryStrings) {
            if (!queryString.contains("<prop")
                    && !queryString.contains("<entity")
                    && !queryString.contains("<literal")
                    && !queryString.contains("<literalArray")
                    && !queryString.contains("<lbound")
                    && !queryString.contains("<rbound")
            ) {
                queries.add(new Query(template, queryString));
            }
        }

        return queries;
    }

    /**
     * Checks if the entity if placed at the slot position (specified with pos)
     * would match the property at that position.
     *
     * @param question for which the query is build
     * @param queryStr where placement should be checked
     * @param entity   that should be placed
     * @param pos      of the entity slot that should be checked
     * @return false if property and entity match, else true
     */
    private boolean isEntityPlacementValid(Question question, String queryStr, String entity, int pos) {
        Matcher matcher = Pattern.compile("(<.*>)\\s*<entity" + pos + ">").matcher(queryStr);
        if (matcher.find()) {
            String prop = matcher.group(1);
            if (question.getLanguageEntities().contains(entity)) {
                return prop.equals("<http://purl.org/dc/terms/language>");
            } else if (question.getLocationEntities().contains(entity)) {
                return prop.equals("<http://purl.org/dc/terms/spatial>");
            } else if (question.getThemeEntities().contains(entity)) {
                return prop.equals("<http://www.w3.org/ns/dcat#theme>");
            } else if (question.getLicenseEntities().contains(entity)) {
                return prop.equals("<http://purl.org/dc/terms/license>");
            } else if (question.getFiletypeEntities().contains(entity)) {
                return prop.equals("<http://purl.org/dc/terms/format>");
            } else if (question.getFrequencyEntities().contains(entity)) {
                return prop.equals("<http://purl.org/dc/terms/accrualPeriodicity>");
            }
        }
        return true;
    }

}

