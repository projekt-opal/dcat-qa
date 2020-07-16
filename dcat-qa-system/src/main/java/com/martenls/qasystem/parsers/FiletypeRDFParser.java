package com.martenls.qasystem.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martenls.qasystem.models.LabeledURI;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class FiletypeRDFParser extends EntityRDFParser {

    private String lastConceptSubject = "";

    public FiletypeRDFParser(String[] languages) {
        super(languages);
    }


    public void parse(String path)  {
        RDFParser parser = new RDFXMLParser();
        parser.setRDFHandler(new OntologieStatementHandler());
        try (InputStream in = new FileInputStream(path)) {
            parser.parse(in, "");
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(new File("src/data/filetypes.json"), parsedEntities.values());
        } catch (IOException e) {
            log.error(e.getMessage());
        }


    }

    protected class OntologieStatementHandler extends AbstractRDFHandler {

        @Override
        public void handleStatement(Statement st) {
            String subject = st.getSubject().stringValue();
            String predicate = st.getPredicate().stringValue();
            String object = st.getObject().stringValue();

            if (object.equals("http://www.w3.org/2004/02/skos/core#Concept")) {
                parsedEntities.put(subject, new LabeledURI(subject.toLowerCase()));
                lastConceptSubject = subject;
            }

            if (predicate.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                Literal l = (Literal) st.getObject();
                Optional<String> lang = l.getLanguage();
                if (lang.isPresent() && languages.contains(lang.get()) && parsedEntities.containsKey(lastConceptSubject)) {
                    parsedEntities.get(lastConceptSubject).getLabels().putIfAbsent(lang.get(), new ArrayList<>());
                    if (!parsedEntities.get(lastConceptSubject).getLabels().get(lang.get()).contains(object.toLowerCase())) {
                        parsedEntities.get(lastConceptSubject).getLabels().get(lang.get()).add(object.toLowerCase());
                    }
                }
            }
        }
    }

    public List<LabeledURI> getParsedEntities() {
        return this.parsedEntities.values().stream()
                .filter(x -> !x.getLabels().isEmpty() && x.getLabels().values().stream().anyMatch(y -> !y.isEmpty()))
                .collect(Collectors.toList());
    }
}
