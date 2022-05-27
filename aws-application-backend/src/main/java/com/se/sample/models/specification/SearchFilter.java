package com.se.sample.models.specification;

import lombok.Data;

@Data
public class SearchFilter {
    private String property;
    private String operator;
    private Object value;
}