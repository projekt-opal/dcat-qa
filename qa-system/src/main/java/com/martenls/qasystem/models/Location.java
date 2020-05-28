package com.martenls.qasystem.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Location {

    @NonNull private String uri;
    private String pref_label;
    private String alt_label;
}
