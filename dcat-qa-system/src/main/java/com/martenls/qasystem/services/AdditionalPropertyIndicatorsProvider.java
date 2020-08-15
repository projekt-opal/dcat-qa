package com.martenls.qasystem.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pemistahl.lingua.api.Language;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Defines lists of words that can be indicators that the correct SPARQL query for the question has to contain certain properties.
 * For example if a question begins with "how many" the correct SPARQL query likely needs to contain a "count" operator.
 */
@Log4j2
@Service
public class AdditionalPropertyIndicatorsProvider {


    private Map<String,Map<String,List<String>>> indicators;

    public AdditionalPropertyIndicatorsProvider(@Value("${data.additionalPropertyIndicators}") String indicatorsFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            indicators = mapper.readValue(Files.readAllBytes(Paths.get(indicatorsFilePath)), new TypeReference<Map<String, Map<String, List<String>>>>(){});
        } catch (IOException e) {
            log.error("Could not read indicators file: {}", e.getMessage());
        }
    }

    /**
     * Get list of indicators for an ascending order of results.
     * @param language of the indicator words
     * @return list of words indicating an asc order in the specified language
     */
    public List<String> getAscIndicators(Language language) {
        return indicators.get("asc_order").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

    /**
     * Get list of indicators for an descending order of results.
     * @param language of the indicator words
     * @return list of words indicating an desc order in the specified language
     */
    public List<String> getDescIndicators(Language language) {
        return indicators.get("desc_order").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

    /**
     * Get list of indicators for a count operator in the query.
     * @param language of the indicator words
     * @return list of words indicating a count operator in the specified language
     */
    public List<String> getCountIndicators(Language language) {
        return indicators.get("count_query").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

    /**
     * Get list of indicators for question asking for the byteSize of dataset/distribution.
     * @param language of the indicator words
     * @return list of words indicating the byteSize property in the specified language
     */
    public List<String> getOrderByByteSizeIndicators(Language language) {
        return indicators.get("order_by_byteSize").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

    /**
     * Get list of indicators for question asking for the byteSize of dataset/distribution.
     * @param language of the indicator words
     * @return list of words indicating the byteSize property in the specified language
     */
    public List<String> getOrderByIssuedIndicators(Language language) {
        return indicators.get("order_by_issued").getOrDefault(language.getIsoCode639_1().toString(), List.of());

    }

    /**
     * Get list of indicators that the question is a yes/no question.
     * @param language of the indicator words
     * @return list of words indicating that the question is a yes/no question.
     */
    public List<String> getAskQueryIndicators(Language language) {
        return indicators.get("ask_query").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

    /**
     * Get list of indicators that the question asks for the byteSize of a distribution.
     * @param language of the indicator words
     * @return list of words indicating that the question asks for the byteSize of a distribution.
     */
    public List<String> getBytesizePropertyIndicators(Language language) {
        return indicators.get("byteSize_property").getOrDefault(language.getIsoCode639_1().toString(), List.of());
    }

}
