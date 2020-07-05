package com.martenls.qasystem.config;


import com.github.pemistahl.lingua.api.Language;

import java.util.Collections;
import java.util.List;

/**
 * Defines lists of words that can be indicators that the correct SPARQL query for the question has to contain certain properties.
 * For example if a question begins with "how many" the correct SPARQL query likely needs to contain a "count" operator.
 */
public class SemanticPropertyIndicators {

    // copied from tebaqa
    private static final List<String> ASC_INDICATORS_EN = List.of("first", "oldest", "smallest", "lowest", "shortest", "least");
    private static final List<String> DESC_INDICATORS_EN = List.of("largest", "last", "highest", "most", "biggest", "youngest", "longest", "tallest", "recently", "most recent");
    private static final List<String> COUNT_INDICATORS_EN = List.of("how many", "how much");

    private static final List<String> ASC_INDICATORS_DE = List.of("erste", "älteste", "kleinste", "niedrigste", "kürzeste", "wenigste");
    private static final List<String> DESC_INDICATORS_DE = List.of("letzte", "jüngste", "neueste", "größte", "höchste", "längste", "meiste", "zuletzt", "letztes", "neulich", "vor kurzem");
    private static final List<String> COUNT_INDICATORS_DE = List.of("wie viele", "wie viel");


    private SemanticPropertyIndicators() {

    }

    /**
     * Get list of indicators for an ascending order of results.
     * @param language of the indicator words
     * @return list of words indicating an asc order in the specified language
     */
    public static List<String> getAscIndicators(Language language) {
        switch (language) {
            case GERMAN:
                return ASC_INDICATORS_DE;
            case ENGLISH:
                return ASC_INDICATORS_EN;
        }
        return Collections.emptyList();
    }

    /**
     * Get list of indicators for an descending order of results.
     * @param language of the indicator words
     * @return list of words indicating an desc order in the specified language
     */
    public static List<String> getDescIndicators(Language language) {
        switch (language) {
            case GERMAN:
                return DESC_INDICATORS_DE;
            case ENGLISH:
                return DESC_INDICATORS_EN;
        }
        return Collections.emptyList();
    }

    /**
     * Get list of indicators for a count operator in the query.
     * @param language of the indicator words
     * @return list of words indicating a count operator in the specified language
     */
    public static List<String> getCountIndicators(Language language) {
        switch (language) {
            case GERMAN:
                return COUNT_INDICATORS_DE;
            case ENGLISH:
                return COUNT_INDICATORS_EN;
        }
        return Collections.emptyList();
    }


}
