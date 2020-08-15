package com.martenls.qasystem.services;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.models.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class QAServiceTest {

    @Autowired
    private QAService qaService;



    @Test
    void answerQuestionLocation() {
        Question question = qaService.answerQuestion(new Question("What datasets exist for Rostock?"));
        assertEquals(Language.ENGLISH, question.getLanguage());
        assertThat(question.getWords(), hasSize(5));
        assertThat(question.getWords(), equalTo(List.of("What", "datasets", "exist", "for", "Rostock")));
        assertThat(question.getWShingles(), hasSize(6));
        assertThat(question.getWShingles(), equalTo(List.of("dataset", "dataset exist", "dataset exist Rostock", "exist", "exist Rostock", "Rostock")));
        assertThat(question.getStringLiterals(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getTimeIntervalEntities(), hasSize(0));
        assertThat(question.getLocationEntities(), hasSize(1));
        assertThat(question.getLocationEntities(), hasItem("http://projekt-opal.de/launuts/lau/DE/13003000"));
        assertThat(question.getLanguageEntities(), hasSize(0));
        assertThat(question.getThemeEntities(), hasSize(0));
        assertThat(question.getLicenseEntities(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getOntologyProperties(), hasSize(2));
        assertThat(question.getOntologyProperties(), hasItem("http://www.w3.org/ns/dcat#dataset"));
        assertThat(question.getOntologyProperties(), hasItem("http://purl.org/dc/terms/spatial"));
        assertThat(question.getQueryCandidates(), hasItem(hasProperty("queryStr", is(equalToCompressingWhiteSpace("  SELECT DISTINCT ?var1  WHERE {      ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.      ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000>  }  ")))));
    }

    @Test
    void answerQuestionCountLocation() {
        Question question = qaService.answerQuestion(new Question("How many datasets exist for Bonn?"));
        assertEquals(Language.ENGLISH, question.getLanguage());
        assertThat(question.getWords(), hasSize(6));
        assertThat(question.getWords(), equalTo(List.of("How", "many", "datasets", "exist", "for", "Bonn")));
        assertThat(question.getWShingles(), hasSize(6));
        assertThat(question.getWShingles(), equalTo(List.of("dataset", "dataset exist", "dataset exist Bonn", "exist", "exist Bonn", "Bonn")));
        assertThat(question.getAdditionalProperties(), hasSize(1));
        assertThat(question.getAdditionalProperties(), hasItem(Question.properties.COUNT));
        assertThat(question.getStringLiterals(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getTimeIntervalEntities(), hasSize(0));
        assertThat(question.getLocationEntities(), hasSize(1));
        assertThat(question.getLocationEntities(), hasItem("http://projekt-opal.de/launuts/lau/DE/05314000"));
        assertThat(question.getLanguageEntities(), hasSize(0));
        assertThat(question.getThemeEntities(), hasSize(0));
        assertThat(question.getLicenseEntities(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getOntologyProperties(), hasSize(2));
        assertThat(question.getOntologyProperties(), hasItem("http://www.w3.org/ns/dcat#dataset"));
        assertThat(question.getOntologyProperties(), hasItem("http://purl.org/dc/terms/spatial"));
        assertThat(question.getQueryCandidates(), hasItem(hasProperty("queryStr", is(equalToCompressingWhiteSpace("  SELECT (COUNT(DISTINCT ?var1) AS ?count)  WHERE {      ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.      ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/05314000>  }  ")))));
    }

    @Test
    void answerQuestionLocationLanguage() {
        Question question = qaService.answerQuestion(new Question("What german datasets exist for Rostock?"));
        assertEquals(Language.ENGLISH, question.getLanguage());
        assertThat(question.getWords(), hasSize(6));
        assertThat(question.getWords(), equalTo(List.of("What", "german", "datasets", "exist", "for", "Rostock")));
        assertThat(question.getWShingles(), hasSize(10));
        assertThat(question.getWShingles(), equalTo(List.of("german", "german dataset", "german dataset exist", "german dataset exist Rostock", "dataset", "dataset exist", "dataset exist Rostock", "exist", "exist Rostock", "Rostock")));
        assertThat(question.getAdditionalProperties(), hasSize(0));
        assertThat(question.getStringLiterals(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getTimeIntervalEntities(), hasSize(0));
        assertThat(question.getLocationEntities(), hasSize(1));
        assertThat(question.getLocationEntities(), hasItem("http://projekt-opal.de/launuts/lau/DE/13003000"));
        assertThat(question.getLanguageEntities(), hasSize(1));
        assertThat(question.getLanguageEntities(), hasItem("http://publications.europa.eu/resource/authority/language/GER"));
        assertThat(question.getThemeEntities(), hasSize(0));
        assertThat(question.getLicenseEntities(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getOntologyProperties(), hasSize(3));
        assertThat(question.getOntologyProperties(), hasItem("http://www.w3.org/ns/dcat#dataset"));
        assertThat(question.getOntologyProperties(), hasItem("http://purl.org/dc/terms/spatial"));
        assertThat(question.getOntologyProperties(), hasItem("http://purl.org/dc/terms/language"));
        assertThat(question.getQueryCandidates(), hasItem(hasProperty("queryStr", is(equalToCompressingWhiteSpace("  SELECT DISTINCT ?var1  WHERE {      ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.      ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000>.      ?var1 <http://purl.org/dc/terms/language> <http://publications.europa.eu/resource/authority/language/GER>.  }")))));
    }

    @Test
    void answerQuestionThemeTimeInterval() {
        Question question = qaService.answerQuestion(new Question("What datasets about transport where published in May 2019?"));
        assertEquals(Language.ENGLISH, question.getLanguage());
        assertThat(question.getWords(), hasSize(9));
        assertThat(question.getWords(), equalTo(List.of("What", "datasets", "about", "transport", "where", "published", "in", "May", "2019")));
        assertThat(question.getWShingles(), hasSize(10));
        assertThat(question.getWShingles(), equalTo(List.of("dataset", "dataset transport", "dataset transport publish", "dataset transport publish 2019", "transport", "transport publish", "transport publish 2019", "publish", "publish 2019", "2019")));
        assertThat(question.getAdditionalProperties(), hasSize(0));
        assertThat(question.getStringLiterals(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getTimeIntervalEntities(), hasSize(1));
        assertThat(question.getLocationEntities(), hasSize(0));
        assertThat(question.getLanguageEntities(), hasSize(0));
        assertThat(question.getThemeEntities(), hasSize(1));
        assertThat(question.getThemeEntities(), hasItem("http://publications.europa.eu/resource/authority/data-theme/TRAN"));
        assertThat(question.getLicenseEntities(), hasSize(0));
        assertThat(question.getTimeEntities(), hasSize(0));
        assertThat(question.getOntologyProperties(), hasSize(3));
        assertThat(question.getOntologyProperties(), hasItem("http://purl.org/dc/terms/issued"));
        assertThat(question.getOntologyProperties(), hasItem("http://www.w3.org/ns/dcat#theme"));
        assertThat(question.getOntologyProperties(), hasItem("http://www.w3.org/ns/dcat#dataset"));
        assertThat(question.getQueryCandidates(), hasItem(hasProperty("queryStr", is(equalToCompressingWhiteSpace("  SELECT DISTINCT ?var1  WHERE {      ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.      ?var1 <http://www.w3.org/ns/dcat#theme> <http://publications.europa.eu/resource/authority/data-theme/TRAN>.      ?var1 <http://purl.org/dc/terms/issued> ?var2.      FILTER ( ?var2 >= \"2019-05-01\"^^<http://www.w3.org/2001/XMLSchema#date> && ?var2 <= \"2019-05-31\"^^<http://www.w3.org/2001/XMLSchema#date>)  }  ")))));
    }

}