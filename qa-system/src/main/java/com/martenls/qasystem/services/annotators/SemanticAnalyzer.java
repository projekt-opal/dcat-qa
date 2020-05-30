package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.String.join;

@Service
public class SemanticAnalyzer implements QuestionAnnotator{


    @Override
    public Question annotate(Question question) {
        question.setWords(getWordsFromString(question.getQuestion()));
        question.setWShingles(getShingles(question.getWords()));
        question.getAdditionProperties().addAll(getAdditionalProperties(question));
        return question;
    }

    public List<String> getWordsFromString(String string) {
        return Arrays.asList(string.replaceAll("[\\-.?¿!,;]", "").split("\\s+"));
    }

    private List<String> getShingles(List<String> strings) {
        List<String> shingles = new ArrayList<>();
        for (int i = 0; i <= strings.size(); i++) {
            for (int y = 1; y <= strings.size() - i; y++) {
                if (y - i < 6) {
                    shingles.add(join(" ", strings.subList(i, i + y)));
                }
            }
        }
        return shingles;
    }

    private Set<Question.properties> getAdditionalProperties(Question question) {
        Set<Question.properties> propertiesSet = new HashSet<>();
        for (String countIndicator : SemanticPropertyIndicatorsEn.COUNT_INDICATORS) {
            if (question.getQuestion().toLowerCase().startsWith(countIndicator)) {
                propertiesSet.add(Question.properties.COUNT);
            }
        }
        if (question.getWords().stream().anyMatch(SemanticPropertyIndicatorsEn.ASC_INDICATORS::contains)) {
            propertiesSet.add(Question.properties.ASC_ORDERED);
        }
        if (question.getWords().stream().anyMatch(SemanticPropertyIndicatorsEn.DESC_INDICATORS::contains)) {
            propertiesSet.add(Question.properties.DESC_ORDERED);
        }
        return propertiesSet;
    }




}
