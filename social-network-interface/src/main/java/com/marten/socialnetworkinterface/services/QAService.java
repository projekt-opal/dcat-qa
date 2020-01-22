package com.marten.socialnetworkinterface.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class QAService {


    public String answerQuestion(String question) {
        return new String(new char[48]).replace("\0", "abcdefghij-");
    }

}
