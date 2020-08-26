package com.martenls.qasystem.parsers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.martenls.qasystem.models.LabeledURI;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Log4j2
public class LabeledURIJsonParser extends EntityRDFParser {

    private List<LabeledURI> parsedEntities;
    private Set<String> languages;

    public LabeledURIJsonParser(String[] languages) {
        super(languages);
    }

    @Override
    public void parse(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            parsedEntities = Arrays.asList(mapper.readValue(Files.readAllBytes(Paths.get(path)), LabeledURI[].class));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public List<LabeledURI> getParsedEntities() {
        return parsedEntities.stream()
                .filter(x -> !x.getLabels().isEmpty() && x.getLabels().values().stream().anyMatch(y -> !y.isEmpty()))
                .collect(Collectors.toList());
    }


}
