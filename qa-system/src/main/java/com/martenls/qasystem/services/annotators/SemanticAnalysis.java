package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.join;

@Service
public class SemanticAnalysis implements QuestionAnnotator{


    @Override
    public Question annotate(Question question) {
        question.setWords(getWordsFromString(question.getQuestion()));
        question.setWShingles(getShingles(question.getWords()));
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




}
