package com.se.sample.service;

import com.se.sample.models.payload.RecognitionLabels;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RekognitionService {
    List<RecognitionLabels> searchLabels(MultipartFile file);
}
