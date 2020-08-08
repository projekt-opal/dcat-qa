package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
public class LabeledURI {

    private String uri;
    private Map<String, List<String>> labels;

    public LabeledURI(@NonNull String uri) {
        this.uri = uri;
        this.labels = new HashMap<>();
    }

    @JsonIgnore
    public LabeledURI getWithLowercasedLabels() {
        return new LabeledURI(this.uri, this.labels.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(String::toLowerCase).collect(Collectors.toList()))));
    }

    @JsonCreator
    public LabeledURI(@NonNull @JsonProperty("uri") String uri, @JsonProperty("labels") Map<String, List<String>> labels) {
        this.uri = uri;
        this.labels = labels;
    }


}

