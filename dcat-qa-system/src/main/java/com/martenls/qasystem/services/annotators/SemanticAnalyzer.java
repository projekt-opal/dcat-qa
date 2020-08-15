package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.services.AdditionalPropertyIndicatorsProvider;
import com.martenls.qasystem.services.StopwordsProvider;
import com.martenls.qasystem.models.Question;
import edu.stanford.nlp.ling.CoreLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.join;

@Service
public class SemanticAnalyzer implements QuestionAnnotator{

    @Autowired
    private StopwordsProvider stopwordsProvider;

    @Autowired
    private AdditionalPropertyIndicatorsProvider indicatorsProvider;

    public SemanticAnalyzer() { }

    @Override
    public Question annotate(Question question) {
        question.setWords(getWordsFromString(question.getQuestionStr()));
        List<String> words = Collections.emptyList();
        if (question.getLanguage() == Language.ENGLISH) {
            words = question.getNlpAnnotations().tokens().stream()
                    .map(CoreLabel::lemma)
                    .collect(Collectors.toList());
        } else if (question.getLanguage() == Language.GERMAN) {
            words = question.getWords();
        }
        // TODO: maybe only filter out shingles that are stopwords
        question.setWShinglesWithStopwords(getShingles(words, 5));
        // filter out stopwords
        words.removeIf(x -> stopwordsProvider.getStopwordsForLang(question.getLanguage()).contains(x.toLowerCase()));
        question.setWShingles(getShingles(words, 5));
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
        String s = string.replaceAll("\".*\"", "");
        s = s.replaceAll("'.*'","");
        s = s.replaceAll("[\\-.?¿!,;\"']", "");
        return new ArrayList<>(Arrays.asList(s.split("\\s+")));
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
        for (String countIndicator : indicatorsProvider.getCountIndicators(question.getLanguage())) {
            if (question.getQuestionStr().toLowerCase().startsWith(countIndicator)) {
                propertiesSet.add(Question.properties.COUNT);
                break;
            }
        }
        for (String askQueryIndicator : indicatorsProvider.getAskQueryIndicators(question.getLanguage())) {
            if (question.getQuestionStr().toLowerCase().startsWith(askQueryIndicator)) {
                propertiesSet.add(Question.properties.ASK_QUERY);
                break;
            }
        }
        if (question.getWords().stream().anyMatch(indicatorsProvider.getAscIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.ASC_ORDERED);
        }
        if (question.getWords().stream().anyMatch(indicatorsProvider.getDescIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.DESC_ORDERED);
        }
        if (question.getWords().stream().anyMatch(indicatorsProvider.getOrderByByteSizeIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.ORDER_BY_BYTESIZE);
        }
        if (question.getWords().stream().anyMatch(indicatorsProvider.getOrderByIssuedIndicators(question.getLanguage())::contains)) {
            propertiesSet.add(Question.properties.ORDER_BY_ISSUED);
        }

        return propertiesSet;
    }

    /**
     * Extracts string literals enclosed with "" or '' from the string.
     * @param string to extract literals from
     * @return list of extracted string literals
     */
    private List<String> getStringLiterals(String string) {
        List<String> results = Pattern.compile("\"([^\"]*)\"(@\\w{2})?")
                .matcher(string)
                .results()
                .map(x -> x.group(0))
                .collect(Collectors.toList());
        results.addAll(Pattern.compile("\'([^\"]*)\'(@\\w{2})?")
                .matcher(string)
                .results()
                .map(x -> x.group(0))
                .map(x -> x.replaceAll("'", "\""))
                .collect(Collectors.toList()));
        return results;
    }




}
