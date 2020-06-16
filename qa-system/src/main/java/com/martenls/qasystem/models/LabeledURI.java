package com.martenls.qasystem.models;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class LabeledURI {

    @NonNull
    private String uri;
    private String label_en;
    private String label_de;


    public void setLabel_en(String label_en) {
        this.label_en = label_en.toLowerCase();
    }

    public void setLabel_de(String label_de) {
        this.label_de = label_de.toLowerCase();
    }
}

