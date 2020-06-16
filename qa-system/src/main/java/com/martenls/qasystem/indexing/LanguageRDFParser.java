package com.martenls.qasystem.indexing;


import com.martenls.qasystem.models.LanguageEntity;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class LanguageRDFParser {

    private final Map<String, LanguageEntity> parsedLanguages;


    public LanguageRDFParser() {
        this.parsedLanguages = new HashMap<>();
    }

    public void parse(String path) {
        RDFParser parser = new RDFXMLParser();
        parser.setRDFHandler(new LanguageRDFParser.OntologieStatementHandler());
        try (InputStream in = new FileInputStream(path)) {
            parser.parse(in, "");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }


    private class OntologieStatementHandler extends AbstractRDFHandler {

        @Override
        public void handleStatement(Statement st) {
            String subject = st.getSubject().stringValue();
            String predicate = st.getPredicate().stringValue();
            String object = st.getObject().stringValue();

            if (object.equals("http://www.w3.org/2004/02/skos/core#Concept")) {
                parsedLanguages.put(subject, new LanguageEntity(subject));
            }

            if (predicate.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
                Literal l = (Literal) st.getObject();
                Optional<String> lang = l.getLanguage();
                if (lang.isPresent() && parsedLanguages.containsKey(subject)) {
                    switch (lang.get()) {
                        case "en":
                            parsedLanguages.get(subject).setLabel_en(object);
                            break;
                        case "de":
                            parsedLanguages.get(subject).setLabel_de(object);
                            break;
                    }
                }


            }
        }
    }

    public List<LanguageEntity> getParsedLanguages() {
        return this.parsedLanguages.values().stream().filter(x -> x.getLabel_en() != null || x.getLabel_de() != null).collect(Collectors.toList());
    }


}
