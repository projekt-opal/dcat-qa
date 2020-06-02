package com.martenls.qasystem.utlis;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPARQL {


    public static Map<String, List<String>> getResultsFromRS(ResultSet resultSet) {
        Map<String, List<String>> results = new HashMap<>();
        resultSet.getResultVars().forEach(x -> results.put(x, new ArrayList<>()));
        resultSet.forEachRemaining(x -> results.keySet().forEach(y -> results.get(y).add(x.get(y).toString())));
        return results;
    }

    public static int getCountFromRS(ResultSet resultSet) {
        return ResultSetFactory.copyResults(resultSet).nextSolution().get(resultSet.getResultVars().get(0)).asLiteral().getInt();
    }
}
