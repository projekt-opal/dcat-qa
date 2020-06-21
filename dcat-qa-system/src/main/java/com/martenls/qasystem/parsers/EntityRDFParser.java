package com.martenls.qasystem.parsers;

import com.martenls.qasystem.models.LabeledURI;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public abstract class EntityRDFParser {

    protected final Map<String, LabeledURI> parsedEntities;

    protected final Set<String> languages;

    public EntityRDFParser(String[] languages) {
        this.parsedEntities = new HashMap<>();
        this.languages = Set.of(languages);
    }

    public void parse(String path) {
        RDFParser parser = new RDFXMLParser();
        parser.setRDFHandler(new EntityRDFParser.OntologieStatementHandler());
        try (InputStream in = new FileInputStream(path)) {
            parser.parse(in, "");
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
                parsedEntities.put(subject, new LabeledURI(subject));
            }

            if (predicate.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
                Literal l = (Literal) st.getObject();
                Optional<String> lang = l.getLanguage();
                if (lang.isPresent() && languages.contains(lang.get()) && parsedEntities.containsKey(subject)) {
                    parsedEntities.get(subject).getLabels().putIfAbsent(lang.get(), new ArrayList<>());
                    parsedEntities.get(subject).getLabels().get(lang.get()).add(object);
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
