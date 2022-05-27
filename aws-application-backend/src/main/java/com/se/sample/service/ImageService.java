package com.se.sample.service;

import com.se.sample.domain.Image;
import com.se.sample.models.response.ImageInformation;
import com.se.sample.models.response.ImageResponse;
import com.se.sample.models.specification.SearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    ImageResponse saveImage(String title, MultipartFile file);

    List<ImageResponse> getAll(String filter);

    List<ImageResponse>search(SearchQuery searchQuery);

    ImageInformation getById(Long id);
}
