package com.martenls.qasystem.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Answer {

    private String answerJsonStr;
    private String queryStr;


    public String getAnswerAsJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode object = mapper.createObjectNode();
        object.put("query", queryStr);
        object.set("answer", mapper.readTree(answerJsonStr));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }


}
