package com.martenls.qasystem.services.annotators;


import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.exceptions.LanguageNotSupportedException;

import com.martenls.qasystem.models.Question;

import com.martenls.qasystem.services.annotators.QuestionAnnotator;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class NLPAnnotator implements QuestionAnnotator {


    private final StanfordCoreNLP enPipeline;

    private final StanfordCoreNLP dePipeline;

    public NLPAnnotator(@Qualifier("getEnPipeline") StanfordCoreNLP enPipeline, @Qualifier("getDePipeline") StanfordCoreNLP dePipeline) {
        this.enPipeline = enPipeline;
        this.dePipeline = dePipeline;
    }




    private StanfordCoreNLP getPipelineByLang(Language language) throws LanguageNotSupportedException {
        switch (language) {
            case ENGLISH:
                return enPipeline;
            case GERMAN:
                return dePipeline;
        }
        throw new LanguageNotSupportedException();
    }

    public Question annotate(Question question) {
        CoreDocument document = new CoreDocument(question.getQuestionStr().replaceAll("[\\-.?¿!,;\"']", ""));
        try {
            getPipelineByLang(question.getLanguage()).annotate(document);
        } catch (LanguageNotSupportedException e) {
            log.error("The language of the question is not supported by the NLP pipeline");
        }
        question.setNlpAnnotations(document);
        return question;
    }

}
