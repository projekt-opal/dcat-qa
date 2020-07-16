
package com.martenls.qasystem.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class StanfordNLPConfig {

    @Value("${data-path}")
    private String dataDirPath;

    @Bean
    public StanfordCoreNLP getEnPipeline() {
        Properties properties = new Properties();
        properties.setProperty("tokenizer.language","en");
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        return new StanfordCoreNLP(properties);
    }

    @Bean
    public StanfordCoreNLP getDePipeline() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/StanfordCoreNLP-german.properties")) {
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        prop.setProperty("sutime.rules", "edu/stanford/nlp/models/sutime/defs.sutime.txt," + dataDirPath + "/german.sutime.txt");
        return new StanfordCoreNLP(prop);
    }
}
