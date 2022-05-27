package com.se.sample.models.specification;

import lombok.Data;

@Data
public class JoinColumnProps {
    private String joinColumnName;
    private SearchFilter searchFilter;
}