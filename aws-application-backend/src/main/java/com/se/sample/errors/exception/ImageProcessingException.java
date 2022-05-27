package com.se.sample.errors.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageProcessingException extends RuntimeException {
    private String message;
}
