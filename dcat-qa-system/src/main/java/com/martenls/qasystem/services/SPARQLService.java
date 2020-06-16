package com.martenls.qasystem.services;



import lombok.extern.log4j.Log4j2;
import org.apache.jena.query.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Log4j2
@Service
public class SPARQLService {

    @Value("${sparql.endpoint}")
    private String endpoint;


    public static String resultSetToString(ResultSet rs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, rs);
        return baos.toString();
    }

    public ResultSet executeQuery(String queryStr) {
        if (!queryStr.toLowerCase().contains("limit")) {
            queryStr += " LIMIT 10";
        }
        queryStr = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + queryStr;

        Query query = QueryFactory.create(queryStr);
        ResultSet rs;

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query) ) {
            qexec.setTimeout(10000l);
            // Execute.
            rs = ResultSetFactory.copyResults(qexec.execSelect());
            return rs;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

}
