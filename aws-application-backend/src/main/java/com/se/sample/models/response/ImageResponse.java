package com.se.sample.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ImageResponse {

    private Long id;
    private String title;
    private String imagePath;
    private String previewImagePath;
    private String imageFileName;
}