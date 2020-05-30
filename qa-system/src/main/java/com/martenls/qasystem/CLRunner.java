package com.martenls.qasystem;

import com.martenls.qasystem.services.annotators.OntologyRecognizer;
import com.martenls.qasystem.services.SPARQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


public class CLRunner implements CommandLineRunner {

    @Autowired
    private OntologyRecognizer ontologyRecognizer;

    @Autowired
    private SPARQLService sparqlService;

    @Override
    public void run(String...args) throws Exception {
        // List<String> props = ontologyRecognizer.recognizeDcatPropertyEn("distribution");

        String rs = sparqlService.executeQueryStr("PREFIX dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
                "SELECT ?x ?y\n" +
                "WHERE { ?x <http://www.w3.org/ns/dcat#distribution> ?y }\n" +
                "LIMIT 10");
        System.out.println(rs);


            //log.info(String.join("; ", message));
            //result.add(message);


    }



}
