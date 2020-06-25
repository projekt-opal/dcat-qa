package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;


import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Data
public class LabeledURI {

    private String uri;
    private Map<String, List<String>> labels;

    public LabeledURI(@NonNull String uri) {
        this.uri = uri;
        this.labels = new HashMap<>();
    }

    @JsonCreator
    public LabeledURI(@NonNull @JsonProperty("uri") String uri, @JsonProperty("labels") Map<String, List<String>> labels) {
        this.uri = uri;
        this.labels = labels;
    }


}

