package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import org.springframework.stereotype.Service;

@Service
public class LanguageRecognizer implements QuestionAnnotator {

    @Override
    public Question annotate(Question question) {
        question.setLanguage("en");
        return question;
    }
}
