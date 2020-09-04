package com.martenls.qasystem.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.martenls.qasystem.models.Answer;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.QAService;
import com.martenls.qasystem.services.SPARQLService;
import com.martenls.qasystem.utils.SPARQLUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.regex.Matcher;

@Log4j2
@RestController
public class QAController {


    @Autowired
    private QAService qaService;

    @Autowired
    private SPARQLService sparqlService;

    static int id = 0;

    @GetMapping("/qa")
    public String answerQuestion(@RequestParam String question) throws JsonProcessingException {
        log.debug("Received question: " + question);
        if (question != null && !question.isBlank()) {
            Answer answer = this.qaService.answerQuestion(new Question(question), 10).getAnswer();
            if (answer == null || answer.getAnswerJsonStr().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return answer.getAnswerAsJSON();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/qa")
    public String gerbilQAEndpoint(@RequestParam String query, @RequestParam String lang, @RequestParam Optional<Integer> qId,  @RequestParam Optional<Integer> resultLimit) {
        log.debug("Received question: " + query);
        id++;
        if (query != null && !query.isBlank()) {
            Question question = resultLimit.isPresent() ? this.qaService.answerQuestion(new Question(query), resultLimit.get()) : this.qaService.answerQuestion(new Question(query));
            Answer answer = question.getAnswer();
            String answerBody = "{\n" +
                    "    \"questions\": [\n" +
                    "      {\n" +
                    "        \"id\": \"" + qId.orElse(id) + "\",\n" +
                    "        \"question\": [\n" +
                    "            {\n" +
                    "                \"language\": \"" + lang + "\",\n" +
                    "                \"string\": \"" + query.replaceAll("\"", Matcher.quoteReplacement("\\\"")) + "\"\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"query\": {\n" +
                    (answer == null ? "": "\"sparql\": \"" + answer.getQueryStr().replaceAll("\n", " ").replaceAll("\\\"", "'") + "\"") + "\n" +
                    "        },\n" +
                    "        \"answers\": [" + (answer == null ? "" : answer.getAnswerJsonStr()) + "]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "}";
//            log.debug(answerBody);
            return answerBody;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/qa/results")
    public String getMoreResults(@RequestParam String query) throws JsonProcessingException {
        if (query != null && !query.isBlank()) {
            query = SPARQLUtils.increaseOffsetByX(query, 10);
            Answer answer = new Answer(SPARQLService.resultSetToString(sparqlService.executeSelectQuery(query, 10)), query);
            if (answer.getAnswerJsonStr().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return answer.getAnswerAsJSON();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }


}
