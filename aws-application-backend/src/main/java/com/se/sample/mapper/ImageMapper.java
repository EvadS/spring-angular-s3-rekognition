package com.se.sample.mapper;

import com.se.sample.domain.Image;
import com.se.sample.models.response.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ImageMapper {


    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);


    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "imageFileName", target = "imageFileName"),
            @Mapping(source = "imagePath", target = "imagePath"),
            @Mapping(source = "previewImagePath", target = "previewImagePath"),
            @Mapping(source = "title", target = "title"), })
    ImageResponse toImageResponse(Image image);

}
