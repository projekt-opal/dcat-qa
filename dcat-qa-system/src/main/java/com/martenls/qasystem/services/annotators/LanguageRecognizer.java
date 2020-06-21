package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import org.springframework.stereotype.Service;


import com.github.pemistahl.lingua.api.*;

import javax.annotation.PostConstruct;

import static com.github.pemistahl.lingua.api.Language.*;


/**
 * Recognizes the language of a question using the lingua library (https://github.com/pemistahl/lingua).
 * Currently only discerns between english and german questions.
 */
@Service
public class LanguageRecognizer implements QuestionAnnotator {


    private LanguageDetector detector;

    @PostConstruct
    private void init() {
        detector = LanguageDetectorBuilder.fromLanguages(ENGLISH, GERMAN).build();
    }



    @Override
    public Question annotate(Question question) {
        question.setLanguage(detector.detectLanguageOf(question.getQuestionStr()));
        return question;
    }


}