package com.martenls.qasystem.models;

import lombok.NonNull;

public class LanguageEntity extends LabeledURI {

    public LanguageEntity(@NonNull String uri) {
        super(uri);
    }
}
