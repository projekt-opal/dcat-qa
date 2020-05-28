package com.martenls.qasystem.services;


import lombok.Cleanup;
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

    public ResultSet executeQueryRS(String queryStr) {
        Query query = QueryFactory.create(queryStr);

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query) ) {
            // Execute.
            ResultSet rs = qexec.execSelect();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
