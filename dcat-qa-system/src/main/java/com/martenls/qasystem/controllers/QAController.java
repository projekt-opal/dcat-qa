package com.martenls.qasystem.controllers;

import com.martenls.qasystem.models.Answer;
import com.martenls.qasystem.services.QAService;
import com.martenls.qasystem.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class QAController {


    @Autowired
    private QAService qaService;

    @GetMapping("/qa")
    public String answerQuestion(@RequestParam String question) {
        if (question != null && !question.isBlank()) {
            Answer answer = this.qaService.answerQuestion(new Question(question)).getAnswer();
            if (answer == null || answer.getAnswerJsonStr().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return answer.getAnswerJsonStr();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/qa")
    public String answerQuestion(@RequestBody Question question) {
        if (question != null && !question.getQuestionStr().isBlank()) {
            Answer answer = this.qaService.answerQuestion(question).getAnswer();
            if (answer == null || answer.getAnswerJsonStr().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return answer.getAnswerJsonStr();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }


}
