package com.martenls.qasystem.models;

import lombok.Data;
import lombok.NonNull;

import java.util.regex.Pattern;

@Data
public class Template {

    @NonNull protected String templateStr;
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
    private boolean stringMatchingFilter;

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("<prop\\d>");
    private static final Pattern ENTITY_PATTERN = Pattern.compile("<entity\\d>");
    private static final Pattern STRING_PATTERN = Pattern.compile("<stringArray\\d>");

    private static final Pattern FILTERSTR_PATTERN = Pattern.compile("filter\\s*\\(\\s*\\?var\\d\\s*in\\s*\\(.*\\)\\s*\\)");


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
        this.stringMatchingFilter = FILTERSTR_PATTERN.matcher(templateStrLowerCase).find();

    }


    public boolean hasCountAggregate() {
        return countAggregate;
    }

    public boolean hasGroupByAggregate() {
        return groupByAggregate;
    }

    public boolean hasHavingAggregate() {
        return havingAggregate;
    }

    public boolean hasOrderDescModifier() {
        return orderDescModifier;
    }

    public boolean hasOrderAscModifier() {
        return orderAscModifier;
    }

    public boolean hasLimitModifier() {
        return limitModifier;
    }

    public boolean hasDistinctModifier() {
        return distinctModifier;
    }

    public boolean hasStringMatchingFilter() { return stringMatchingFilter; }
}
