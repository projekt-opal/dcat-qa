package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexing.LanguageIndexer;
import com.martenls.qasystem.indexing.LanguageRDFParser;
import com.martenls.qasystem.indexing.OntologyIndexer;
import com.martenls.qasystem.indexing.OntologyRDFParser;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LanguageEntityRecognizer implements QuestionAnnotator{

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.language_index}")
    private String languageIndex;

    @Value("${languages.file}")
    private String languageRDFPath;

    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present.
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(languageIndex)) {
                LanguageRDFParser parser = new LanguageRDFParser();
                parser.parse(languageRDFPath);
                LanguageIndexer indexer = new LanguageIndexer(searchService, languageIndex);
                if (!searchService.checkIndexExistence(languageIndex)) {
                    indexer.indexLanguages(parser.getParsedLanguages());
                }

            } else {
                log.debug("Language-index present, nothing to be done");
            }
        } catch (ESIndexUnavailableException e) {
            log.error("Could not init indices: ESIndex not available");
        }

    }

    /**
     * Annotates the question with all properties and classes that match at least one of the w-shingles.
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getLanguageEntities().addAll(recognizeLanguageEntities(shingle, question.getLanguage()));
            }
        }
        return question;
    }


    /**
     * Queries the language index for the given word in the given language.
     * @param word to query
     * @param language to query in
     * @return list of matched properties
     */
    private List<String> recognizeLanguageEntities(String word, Language language) {
        try {
            return searchService.queryIndex("label_" + language.getIsoCode639_1().toString(), word.toLowerCase(), languageIndex, 10, "1").stream()
                    .map(x -> x.get("uri"))
                    .collect(Collectors.toList());
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch language entities: ESIndex not available");
            return Collections.emptyList();
        }
    }


}
