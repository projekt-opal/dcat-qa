package com.martenls.qasystem.models;

import lombok.Data;
import lombok.NonNull;

import java.util.regex.Pattern;

@Data
public class Template {

    @NonNull private String templateStr;
    private int propertyCount;
    private int entityCount;
    private int stringArrayCount;
    private boolean countAggregate;
    private boolean groupByAggregate;
    private boolean havingAggregate;
    private boolean orderDescModifier;
    private boolean orderAscModifier;
    private boolean limitModifier;
    private boolean distinctModifier;

    private final static Pattern PROPERTY_PATTERN = Pattern.compile("<prop\\d>");
    private final static Pattern ENTITY_PATTERN = Pattern.compile("<entity\\d>");
    private final static Pattern STRING_PATTERN = Pattern.compile("<stringArray\\d>");


    public Template(String templateStr) {
        this.templateStr = templateStr;
        String templateStrLowerCase = templateStr.toLowerCase();

        this.propertyCount = (int) PROPERTY_PATTERN.matcher(templateStr).results().count();
        this.entityCount = (int) ENTITY_PATTERN.matcher(templateStr).results().count();
        this.stringArrayCount = (int) STRING_PATTERN.matcher(templateStr).results().count();

        this.countAggregate = templateStrLowerCase.contains("count");
        this.groupByAggregate = templateStrLowerCase.contains("group by");
        this.havingAggregate = templateStrLowerCase.contains("having");
        if (templateStrLowerCase.contains("order by")) {
            if (templateStrLowerCase.contains("desc")) {
                this.orderDescModifier = true;
            } else {
                this.orderAscModifier = true;
            }
        }
        this.limitModifier = templateStrLowerCase.contains("limit");
        this.distinctModifier = templateStrLowerCase.contains("distinct");

    }

}
