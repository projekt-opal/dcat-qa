package com.martenls.qasystem.indexing;

import com.martenls.qasystem.models.Location;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Log4j2
public class LaunutsRDFParser {

    private final Map<String, Location> parsedLocations;

    public LaunutsRDFParser() {
        this.parsedLocations = new HashMap<>();
    }

    public void parse(String path) {
        TurtleParser parser = new TurtleParser();
        parser.setRDFHandler(new LaunutsRDFParser.LaunutsStatementHandler());
        try (InputStream in = new FileInputStream(path)) {
            parser.parse(in, "");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }


    private class LaunutsStatementHandler extends AbstractRDFHandler {
        @Override
        public void handleStatement(Statement st) {
            String subject = st.getSubject().stringValue();
            String predicate = st.getPredicate().stringValue();
            String object = st.getObject().stringValue();

            if (object.equals("http://projekt-opal.de/launuts/LAU")) {
                parsedLocations.put(subject, new Location(subject));
            }
            if (parsedLocations.containsKey(subject)) {
                if (predicate.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
                    parsedLocations.get(subject).setPref_label(object);
                }
                if (predicate.equals("http://www.w3.org/2004/02/skos/core#altLabel")) {
                    parsedLocations.get(subject).setAlt_label(object);
                }
            }

        }
    }

    public List<Location> getParsedLocations() {
        return parsedLocations.values().stream()
                .filter(x -> x.getPref_label() != null || x.getAlt_label() != null)
                .collect(Collectors.toList());
    }
}
