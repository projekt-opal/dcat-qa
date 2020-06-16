package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.config.SemanticPropertyIndicators;
import com.martenls.qasystem.exceptions.LanguageNotSupportedException;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.NLPService;
import edu.stanford.nlp.pipeline.CoreDocument;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.join;

@Service
public class SemanticAnalyzer implements QuestionAnnotator{

    private NLPService nlpService;

    public SemanticAnalyzer(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @Override
    public Question annotate(Question question) {
        question.setWords(getWordsFromString(question.getQuestionStr()));
        question.setWShingles(getShingles(question.getWords(), 5));
        question.getAdditionalProperties().addAll(getAdditionalProperties(question));
        question.getStringLiterals().addAll(getStringLiterals(question.getQuestionStr()));
        return question;
    }

    /**
     * Removes punctuation and splits string at whitespace.
     * @param string to get words from
     * @return list of single words from string
     */
    public List<String> getWordsFromString(String string) {
        return Arrays.asList(string.replaceAll("[\\-.?¿!,;\"']", "").split("\\s+"));
    }

    /**
     * Returns list of all w-shingles for w in [0, n].
     * For example for ["How", "many", "datasets"] and n=2 it returns:
     * ["How", "How many", "many", "many datasets", "datasets"]
     * @param strings list of words to get shingles from
     * @param n maximum size of shingle
     * @return list of all w-shingles for w in [0, n] where one shingle is one string
     */
    private List<String> getShingles(List<String> strings, int n) {
        List<String> shingles = new ArrayList<>();
        for (int i = 0; i <= strings.size(); i++) {
            for (int y = 1; y <= strings.size() - i; y++) {
                if (y - i <= n) {
                    shingles.add(join(" ", strings.subList(i, i + y)));
                }
            }
        }
        return shingles;
    }

    /**
     * Checks question for additional property indicators like the phrase "how many" which would indicate count query.
     * @param question to analyze
     * @return Set of found properties
     */
    private Set<Question.properties> getAdditionalProperties(Question question) {
        Set<Question.properties> propertiesSet = new HashSet<>();
        for (String countIndicator : SemanticPropertyIndicators.getCountIndicators(question.getLanguage())) {
            if (question.getQuestionStr().toLowerCase().startsWith(countIndicator)) {
                propertiesSet.add(Question.properties.COUNT);
            }
        }
        if (question.getWords().stream().anyMatch(SemanticPropertyIndicators.getAscIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.ASC_ORDERED);
        }
        if (question.getWords().stream().anyMatch(SemanticPropertyIndicators.getDescIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.DESC_ORDERED);
        }
        return propertiesSet;
    }

    /**
     * Extracts string literals enclosed with "" or '' from the string.
     * @param string to extract literals from
     * @return list of extracted string literals
     */
    private List<String> getStringLiterals(String string) {
        List<String> results = Pattern.compile("\"([^\"]*)\"")
                .matcher(string)
                .results()
                .map(x -> x.group(1))
                .collect(Collectors.toList());
        results.addAll(Pattern.compile("\'([^\"]*)\'")
                .matcher(string)
                .results()
                .map(x -> x.group(1))
                .collect(Collectors.toList()));
        return results;
    }




}
