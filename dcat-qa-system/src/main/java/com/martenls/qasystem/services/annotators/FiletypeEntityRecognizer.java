package com.martenls.qasystem.services.annotators;


import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.LabeledURIJsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class FiletypeEntityRecognizer extends EntityRecognizer {

    public FiletypeEntityRecognizer(@Value("${es.filetype_index}") String indexName,
                                    @Value("${data.filetypeEntities}") String rdfFilePath,
                                    @Value("${properties.languages}") String[] languages
    ) {
        super(indexName, rdfFilePath, languages, new LabeledURIJsonParser(languages));
    }


    /**
     * Annotates the question with all properties and classes that match at least one of the w-shingles.
     *
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getFiletypeEntities().addAll(recognizeEntities(shingle, Language.ENGLISH, 1, "1"));
            }
        }
        return question;
    }
}
