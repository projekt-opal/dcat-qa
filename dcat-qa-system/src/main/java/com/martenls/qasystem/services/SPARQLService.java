package com.martenls.qasystem.services;


import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Log4j2
@Service
public class SPARQLService {

    private final String endpoint;

    private final String queryPath;

    public SPARQLService(@Value("${sparql.endpoint}") String endpoint, @Value("${sparql.queryPath}") String queryPath) throws InterruptedException {
        this.endpoint = endpoint;
        this.queryPath = queryPath;

        boolean fusekiUnavailable = true;
        int i = 0;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint + "/$/ping");
        while (fusekiUnavailable && i < 10) {
            try {
                CloseableHttpResponse response1 = httpclient.execute(httpGet);
                fusekiUnavailable = false;
            } catch (IOException e) {
                log.error("Fuseki instance can not be reached. Will try again in 2 seconds... ");
                i++;
                Thread.sleep(2000);
            }
        }
        if (fusekiUnavailable) {
            log.error("Fuseki instance can not be reached. Shutting down...");
            System.exit(1);
        } else {
            log.info("Fuseki connection successful");
        }
    }

    public static String resultSetToString(ResultSet rs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, rs);
        return baos.toString();
    }

    public ResultSet executeSelectQuery(String queryStr, int resultLimit) {
        // limit all queries to 10 results
        if (!queryStr.toLowerCase().contains("limit") && resultLimit > 0) {
            queryStr += " LIMIT " + resultLimit;
        }

        // add prefixes
        queryStr = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + queryStr;

        Query query = QueryFactory.create(queryStr);

        ResultSet rs;

        // Remote execution.
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint + queryPath, query)) {
            qexec.setTimeout(10000L);
            rs = ResultSetFactory.copyResults(qexec.execSelect());
            return rs;
        } catch (QueryExceptionHTTP e) {
            log.error("Sparql endpoint timeout while executing {}", query);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    public Boolean executeAskQuery(String queryStr) {
        // add prefixes
        queryStr = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + queryStr;
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint + queryPath, queryStr)) {
            qexec.setTimeout(10000L);
            return qexec.execAsk();
        } catch (QueryExceptionHTTP e) {
            log.error("Sparql endpoint timeout while executing {}", queryStr);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

}
