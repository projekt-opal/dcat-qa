package com.martenls.qasystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TemplateRated {

    private Template template;
    private int rating;

}
