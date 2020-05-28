package com.martenls.qasystem.controllers;

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
    public String answerQuestion(@RequestParam String q) {
        String answer = this.qaService.answerQuestion(new Question(q)).toString();
        if (answer == null || answer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return answer;
        }
    }


}
