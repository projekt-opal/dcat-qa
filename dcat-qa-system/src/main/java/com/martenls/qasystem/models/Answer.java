package com.martenls.qasystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Answer {

    private String answerJsonStr;
    private Query query;


}
