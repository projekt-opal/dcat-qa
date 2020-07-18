package com.martenls.qasystem.parsers;

import com.martenls.qasystem.models.LabeledURI;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



@Log4j2
public class LaunutsRDFParser extends EntityRDFParser {


    public LaunutsRDFParser() {
        super(new String[]{"de"});
    }

    @Override
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
                parsedEntities.put(subject, new LabeledURI(subject));
            }
            if (parsedEntities.containsKey(subject) &&
                    (predicate.equals("http://www.w3.org/2004/02/skos/core#prefLabel") ||
                            predicate.equals("http://www.w3.org/2004/02/skos/core#altLabel"))) {
                parsedEntities.get(subject).getLabels().putIfAbsent("de", new ArrayList<>());
                parsedEntities.get(subject).getLabels().get("de").add(object);

            }

        }
    }


}
