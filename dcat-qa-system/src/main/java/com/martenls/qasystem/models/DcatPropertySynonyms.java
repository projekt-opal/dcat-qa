package com.martenls.qasystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
public class DcatPropertySynonyms {

    private String uri;
    private List<String> labels_en;
    private List<String> labels_de;

    @JsonCreator
    public DcatPropertySynonyms(@JsonProperty("uri")String  uri, @JsonProperty("labels_en") List<String> labels_en, @JsonProperty("labels_de") List<String> labels_de) {
        this.uri = uri;
        this.labels_en = labels_en;
        this.labels_de = labels_de;
    }
}
