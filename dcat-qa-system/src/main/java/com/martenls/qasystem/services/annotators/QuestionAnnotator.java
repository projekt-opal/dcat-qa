package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import org.springframework.stereotype.Service;

@Service
public interface QuestionAnnotator {

    Question annotate(Question question);

}
