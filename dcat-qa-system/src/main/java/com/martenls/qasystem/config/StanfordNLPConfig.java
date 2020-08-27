package com.martenls.qasystem.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
@Configuration
public class StanfordNLPConfig {

    @Value("${data.dir}")
    private String dataDirPath;

    @Bean
    public StanfordCoreNLP getEnPipeline() {
        Properties properties = new Properties();
        properties.setProperty("tokenizer.language", "en");
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        properties.setProperty("ner.docdate.usePresent", "true");
        return new StanfordCoreNLP(properties);
    }

    @Bean
    public StanfordCoreNLP getDePipeline() {
        Properties properties = new Properties();
        try (InputStream input = new ClassPathResource("StanfordCoreNLP-german.properties").getInputStream()) {
            // load a properties file
            properties.load(input);
        } catch (IOException e) {
            log.error("Could not load property file for german Stanford NLP pipeline", e);
        }
        properties.setProperty("sutime.rules", "edu/stanford/nlp/models/sutime/defs.sutime.txt," + dataDirPath + "/german.sutime.txt");
        properties.setProperty("ner.docdate.usePresent", "true");
        return new StanfordCoreNLP(properties);
    }
}
