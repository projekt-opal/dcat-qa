package com.martenls.qasystem.models;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.jena.query.ResultSet;

@RequiredArgsConstructor
@Data
public class Query {

    @NonNull
    private Template template;
    @NonNull
    private String queryStr;
    private ResultSet selectResult;
    private Boolean askResult;

}
