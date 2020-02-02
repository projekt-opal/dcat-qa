package com.martenls.qasystem.controllers;

import com.martenls.qasystem.dtos.Question;
import org.springframework.web.bind.annotation.*;

@RestController
public class QAController {

    @PostMapping("/qa")
    public String answerQuestion(@RequestBody Question q) {
        System.out.println(q.getQuestion());
        return "{\n" +
                "  \"question\":"+ "\"" + q.getQuestion()+ "\"" + ",\n" +
                "  \"head\": { \"vars\": [ \"book\" , \"title\" ]\n" +
                "  } ,\n" +
                "  \"results\": { \n" +
                "    \"bindings\": [\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book6\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Half-Blood Prince\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book7\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Deathly Hallows\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book5\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Order of the Phoenix\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book4\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Goblet of Fire\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book2\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Chamber of Secrets\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book3\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Prisoner Of Azkaban\" }\n" +
                "      } ,\n" +
                "      {\n" +
                "        \"book\": { \"type\": \"uri\" , \"value\": \"http://example.org/book/book1\" } ,\n" +
                "        \"title\": { \"type\": \"literal\" , \"value\": \"Harry Potter and the Philosopher's Stone\" }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }


}
