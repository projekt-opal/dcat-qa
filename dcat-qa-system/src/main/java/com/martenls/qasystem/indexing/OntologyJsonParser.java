package com.martenls.qasystem.indexing;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.martenls.qasystem.models.DcatPropertySynonyms;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@Log4j2
public class OntologyJsonParser {


    private List<DcatPropertySynonyms> parsedProperties;

    public void parse(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            parsedProperties = Arrays.asList(mapper.readValue(Files.readAllBytes(Paths.get(path)), DcatPropertySynonyms[].class));
        } catch (IOException e) {
            log.error(e);
        }
    }
}
