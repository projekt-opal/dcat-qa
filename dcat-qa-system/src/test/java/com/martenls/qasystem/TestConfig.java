package com.martenls.qasystem;

import com.martenls.qasystem.services.SPARQLService;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ResultSetFactory;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    @Primary
    public SPARQLService sparqlService() throws IOException {
        SPARQLService mockSparqlService = Mockito.mock(SPARQLService.class);
        Mockito.when(mockSparqlService.executeQuery(anyString())).thenReturn(ResultSetFactory.fromJSON(IOUtils.toInputStream("{ \"head\": {\n" +
                "    \"vars\": [ \"var0\" ]\n" +
                "  } ,\n" +
                "  \"results\": {\n" +
                "    \"bindings\": []\n" +
                "  }\n" +
                "}", "UTF-8") ));
        return mockSparqlService;
    }
}
