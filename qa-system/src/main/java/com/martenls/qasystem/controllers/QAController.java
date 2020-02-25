package com.martenls.qasystem.controllers;

import com.martenls.qasystem.QAService;
import com.martenls.qasystem.dtos.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class QAController {


    @Autowired
    private QAService qaService;

    @PostMapping("/qa")
    public String answerQuestion(@RequestBody Question q) {
        System.out.println(q.getQuestion());
        String answer = this.qaService.getAnswer(q.getTries());
        if (answer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return String.format(answer, q.getQuestion());
        }
    }


}
