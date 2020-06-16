
package com.martenls.qasystem.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class StanfordNLPConfig {

    @Bean
    public StanfordCoreNLP getEnPipeline() {
        Properties properties = new Properties();
        properties.setProperty("tokenizer.language","en");
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        return new StanfordCoreNLP(properties);
    }

    @Bean
    public StanfordCoreNLP getDePipeline() {
        return new StanfordCoreNLP("german");
    }
}
