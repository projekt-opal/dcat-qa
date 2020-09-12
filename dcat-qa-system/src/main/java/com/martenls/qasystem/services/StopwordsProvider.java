package com.martenls.qasystem.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pemistahl.lingua.api.Language;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

@Log4j2
@Service
public class StopwordsProvider {

    private Map<String, Set<String>> stopwords;

    /**
     * Reads in stopwords from the specified file.
     * @param stopwordsFilePath path to the stopwords file.
     */
    public StopwordsProvider(@Value("${data.stopwords}") String stopwordsFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            stopwords = mapper.readValue(Files.readAllBytes(Paths.get(stopwordsFilePath)), new TypeReference<Map<String, Set<String>>>() {
            });
        } catch (IOException e) {
            log.error("Could not read stopwords file: {}", e.getMessage());
        }
    }

    public Set<String> getStopwordsForLang(Language language) {
        return stopwords.getOrDefault(language.getIsoCode639_1().toString(), Set.of());
    }
}
