package com.martenls.qasystem.services;



import org.apache.jena.query.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class SPARQLService {

    @Value("${sparql.endpoint}")
    private String endpoint;


    public String executeQueryStr(String queryStr) {
        ResultSet rs = this.executeQueryRS(queryStr);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, rs);
        return baos.toString();
    }

    public static String resultSetToString(ResultSet rs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, rs);
        return baos.toString();
    }

    public ResultSet executeQueryRS(String queryStr) {
        if (!queryStr.toLowerCase().contains("limit")) {
            queryStr += " LIMIT 10";
        }

        Query query = QueryFactory.create(queryStr);
        ResultSet rs;

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query) ) {
            // Execute.
            rs = ResultSetFactory.copyResults(qexec.execSelect());
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
