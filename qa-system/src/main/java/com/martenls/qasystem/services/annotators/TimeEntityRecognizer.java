package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.exceptions.LanguageNotSupportedException;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.NLPService;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TimeEntityRecognizer implements QuestionAnnotator{

    @Autowired
    private NLPService nlpService;

    @Override
    public Question annotate(Question question) {
        CoreDocument document = nlpService.annotate(question);

        for (CoreEntityMention entityMention : document.entityMentions()) {
            switch (entityMention.entityType()) {
                case "DATE":
                    Timex timex = entityMention.coreMap().get(TimeAnnotations.TimexAnnotation.class);
                    if (timex.getRange() != null) {
                        question.getTimeIntervalEntities().add(timex.getRange());
                    } else if (timex.getDate() != null) {
                        question.getTimeEntities().add(timex.getDate());
                    }
                    break;
                case "SET":
                    //TODO implement frequency recognition
                    break;
            }
        }
        return question;
    }
}
