package com.martenls.qasystem.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.martenls.qasystem.models.Answer;
import com.martenls.qasystem.services.QAService;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.SPARQLService;
import com.martenls.qasystem.utils.SPARQLUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
public class QAController {


    @Autowired
    private QAService qaService;

    @Autowired
    private SPARQLService sparqlService;

    @GetMapping("/qa")
    public String answerQuestion(@RequestParam String question) throws JsonProcessingException {
        log.debug("Received question: " + question);
        if (question != null && !question.isBlank()) {
            Answer answer = this.qaService.answerQuestion(new Question(question)).getAnswer();
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
    public String answerQuestion(@RequestBody Question question) throws JsonProcessingException {
        if (question != null && !question.getQuestionStr().isBlank()) {
            log.debug("Received question: " + question.getQuestionStr());
            Answer answer = this.qaService.answerQuestion(question).getAnswer();
            if (answer == null || answer.getAnswerJsonStr().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return answer.getAnswerAsJSON();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/qa/results")
    public String getMoreResults(@RequestParam String query) throws JsonProcessingException {
        if (query != null && !query.isBlank()) {
            query = SPARQLUtils.increaseOffsetByX(query, 10);
            Answer answer = new Answer(SPARQLService.resultSetToString(sparqlService.executeQuery(query)), query);
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
