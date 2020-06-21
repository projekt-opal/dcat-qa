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
public class ThemesRDFParser extends EntityRDFParser {

    public ThemesRDFParser(String[] languages) {
        super(languages);
    }

    @Override
    public void parse(String path) {
        RDFParser parser = new RDFXMLParser();
        parser.setRDFHandler(new ThemesRDFParser.OntologieStatementHandler());
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
                    parsedEntities.get(subject).getLabels().get(lang.get()).addAll(splitThemeString(object, lang.get()));
                }


            }
        }

        private List<String> splitThemeString(String string, String language) {
            switch (language) {
                case "en":
                    return Arrays.stream(string.split("(and|,)")).map(String::toLowerCase).map(String::trim).collect(Collectors.toList());
                case "de":
                    return Arrays.stream(string.split("(und|,)")).map(String::trim).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }


}
