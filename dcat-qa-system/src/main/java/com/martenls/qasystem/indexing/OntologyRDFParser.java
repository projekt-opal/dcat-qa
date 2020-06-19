package com.martenls.qasystem.indexing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.martenls.qasystem.models.DcatClass;
import com.martenls.qasystem.models.DcatProperty;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import org.eclipse.rdf4j.rio.rdfxml.RDFXMLParser;

import java.io.*;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class OntologyRDFParser {

    private final Map<String, DcatClass> parsedClasses;
    private final Map<String, DcatProperty> parsedProperties;

    public OntologyRDFParser() {
        this.parsedClasses = new HashMap<>();
        this.parsedProperties = new HashMap<>();
    }

    public void parse(String path) {
        RDFParser parser = new RDFXMLParser();
        parser.setRDFHandler(new OntologieStatementHandler());
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

            if (object.equals("http://www.w3.org/2002/07/owl#Class")) {
                parsedClasses.put(subject, new DcatClass(subject));
            }
            if (object.equals("http://www.w3.org/2002/07/owl#ObjectProperty") || object.equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {
                parsedProperties.put(subject, new DcatProperty(subject));
            }
            if (predicate.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                Literal l = (Literal) st.getObject();
                Optional<String> lang = l.getLanguage();
                if (lang.isPresent() && lang.get().equals("en")) {
                    if (parsedClasses.containsKey(subject)) {
                        parsedClasses.get(subject).setLabel_en(object);
                    } else if (parsedProperties.containsKey(subject)) {
                        parsedProperties.get(subject).setLabel_en(object);
                    }
                } else if (lang.isPresent() && lang.get().equals("de")) {
                    if (parsedClasses.containsKey(subject)) {
                        parsedClasses.get(subject).setLabel_de(object);
                    } else if (parsedProperties.containsKey(subject)) {
                        parsedProperties.get(subject).setLabel_de(object);
                    }
                }


                }
        }
    }

    public List<DcatClass> getParsedClasses() {
        return this.parsedClasses.values().stream().filter(x -> x.getLabel_en() != null || x.getLabel_de() != null).collect(Collectors.toList());
    }

    public List<DcatProperty> getParsedProperties() {
        return this.parsedProperties.values().stream().filter(x -> x.getLabel_en() != null || x.getLabel_de() != null).collect(Collectors.toList());
    }

}
