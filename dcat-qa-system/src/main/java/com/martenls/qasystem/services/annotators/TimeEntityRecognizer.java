package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import org.springframework.stereotype.Service;


@Service
public class TimeEntityRecognizer implements QuestionAnnotator{


    @Override
    public Question annotate(Question question) {
        CoreDocument document = question.getNlpAnnotations();

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
