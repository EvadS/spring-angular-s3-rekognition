package com.se.sample.models.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecognitionLabels {
    private String name;
    private Float confidence;
}
