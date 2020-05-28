package com.martenls.qasystem.services;


import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TemplateSelector {

    @Autowired
    private TemplateProvider templateProvider;

    private int scoreTemplateQuestionPair(Template template, Question question) {
        int score = 0;

        if (template.getPropertyCount() == question.getOntologyProperties().size()) {
            score += 10;
        }

        // TODO: add more rules

        // "how many, the most, the least" -> count() / group by + order by


        return score;
    }
}
