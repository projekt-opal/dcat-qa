package com.martenls.qasystem.utils;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SPARQLUtils {

    private SPARQLUtils() { }

    /**
     * Transforms a result set to a map of the result variables and their values.
     * @param resultSet to transform
     * @return map of result variables and their values
     */
    public static Map<String, List<String>> getResultsFromRS(ResultSet resultSet) {
        Map<String, List<String>> results = new HashMap<>();
        resultSet.getResultVars().forEach(x -> results.put(x, new ArrayList<>()));
        resultSet.forEachRemaining(x -> results.keySet().forEach(y -> results.get(y).add(x.get(y).toString())));
        return results;
    }

    /**
     * Extracts the number from a result set of a count query.
     * @param resultSet to extract number from
     * @return the result of the count query as number
     */
    public static int getCountFromRS(ResultSet resultSet) {
        return ResultSetFactory.copyResults(resultSet).nextSolution().get(resultSet.getResultVars().get(0)).asLiteral().getInt();
    }

    /**
     * Increases the offset value in a query string by x.
     * @param queryStr to increase the offset of
     * @param x to increase the offset by
     * @return query string with increased offset
     */
    public static String increaseOffsetByX(String queryStr, int x) {
        if (!queryStr.toLowerCase().contains("offset")) {
            queryStr += "\n OFFSET " + x;
        } else {
            Pattern offsetPattern = Pattern.compile("offset\\s*(\\d+)");
            Matcher matcher = offsetPattern.matcher(queryStr.toLowerCase());
            if (matcher.find()) {
                int offset = Integer.parseInt(matcher.group(1));
                offset += x;
                queryStr = queryStr.replaceAll("(?i)offset\\s*(\\d+)", String.format("OFFSET %d", offset));
            }

        }
        return queryStr;
    }
}
