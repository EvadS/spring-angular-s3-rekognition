package com.se.sample.controller;


import com.se.sample.config.RestConfig;
import com.se.sample.constraint.ContentType;
import com.se.sample.models.response.ImageInformation;
import com.se.sample.models.response.ImageResponse;
import com.se.sample.models.specification.SearchQuery;
import com.se.sample.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(RestConfig.IMAGE_CONTROLLER_API)
@AllArgsConstructor
@CrossOrigin("*")
@Validated
public class ImageController {
    private final ImageService imageService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam(RestConfig.TITLE_REQUEST_PARAM) String title,
                                                     @Valid @ContentType({"JPG", "JPEG", "PNG", "BMP"})
                                                     @RequestParam(RestConfig.FILE_REQUEST_PARAM) MultipartFile file) {
        ImageResponse imageResponse = imageService.saveImage(title, file);
        return ResponseEntity.ok(imageResponse);
    }

    @RequestMapping(value = RestConfig.SEARCH_API, method = RequestMethod.GET)
    public ResponseEntity<List<ImageResponse>> getAll(@RequestParam(required = false, defaultValue = "")
                                                                  String filter) {
        List<ImageResponse> searchResponse = imageService.getAll(filter);
        return ResponseEntity.ok(searchResponse);
    }

    @PostMapping(RestConfig.SEARCH_API)
    public ResponseEntity<List<ImageResponse>> searchByQuery(@RequestBody SearchQuery searchQuery) {
        List<ImageResponse> searchResponse =imageService.search(searchQuery);
        return ResponseEntity.ok(searchResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageInformation> getById(@PathVariable(value = "id") Long id) {
        ImageInformation imageInformation = imageService.getById(id);
        return ResponseEntity.ok().body(imageInformation);
    }
}
