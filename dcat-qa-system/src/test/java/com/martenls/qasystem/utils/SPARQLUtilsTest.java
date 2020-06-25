package com.martenls.qasystem.utils;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SPARQLUtilsTest {

    private ResultSet rs;

    @BeforeEach
    void init() throws IOException {
        rs = ResultSetFactory.fromJSON(IOUtils.toInputStream("{ \"head\": {\n" +
                "    \"vars\": [ \"count\" ]\n" +
                "  } ,\n" +
                "  \"results\": {\n" +
                "    \"bindings\": [\n" +
                "      { \n" +
                "        \"count\": { \"type\": \"literal\" , \"datatype\": \"http://www.w3.org/2001/XMLSchema#integer\" , \"value\": \"11368\" }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}", "UTF-8") );
    }

    @Test
    void getResultsFromRS() {
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("count", List.of("11368^^http://www.w3.org/2001/XMLSchema#integer"));
        assertEquals(expected, SPARQLUtils.getResultsFromRS(rs));
    }

    @Test
    void getCountFromRS() {
        assertEquals(11368, SPARQLUtils.getCountFromRS(rs));
    }
}