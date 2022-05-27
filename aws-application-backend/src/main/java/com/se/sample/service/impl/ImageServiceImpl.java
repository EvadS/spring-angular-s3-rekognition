package com.se.sample.service.impl;


import com.amazonaws.services.appstream.model.ImageBuilder;
import com.se.sample.config.BucketName;
import com.se.sample.domain.Image;
import com.se.sample.domain.ImageLabel;
import com.se.sample.errors.exception.FileStorageException;
import com.se.sample.errors.exception.ImageProcessingException;
import com.se.sample.errors.exception.ResourceNotFoundException;
import com.se.sample.mapper.ImageMapper;
import com.se.sample.models.payload.ConvertToMultipartFile;
import com.se.sample.models.payload.RecognitionLabels;
import com.se.sample.models.payload.ResizeModel;
import com.se.sample.models.response.ImageInformation;
import com.se.sample.models.response.ImageResponse;
import com.se.sample.models.response.RecognitionResponse;
import com.se.sample.models.specification.SearchQuery;
import com.se.sample.repository.ImageRepository;
import com.se.sample.service.FileStore;
import com.se.sample.service.ImageService;
import com.se.sample.service.RekognitionService;
import com.se.sample.util.ImageUtils;
import com.se.sample.util.SpecificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.se.sample.util.Constants.DEFAULT_FILE_EXTENSION;


@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final FileStore fileStore;
    private final ImageRepository repository;
    private final RekognitionService rekognitionService;

    @Value("${image.preview.height:150}")
    private int previewHeight;

    @Value("${image.preview.weight:150}")
    private int previewWidth;

    @Override
    public ImageResponse saveImage(String title, MultipartFile file) {
        log.info("save image, file:{}, title:{}", file.getOriginalFilename(), title);
        String fileName = String.format("%s", file.getOriginalFilename());

        try {
            String uuidName = UUID.randomUUID().toString();
            fileStore.uploadFile(BucketName.IMAGE_BUCKET_NAME.getBucketName(), uuidName, ImageUtils.ConvertMultiPartToFile(file));

            String fileUrl = fileStore.getUrl(BucketName.IMAGE_BUCKET_NAME.getBucketName(), uuidName);
            log.info("Uploaded to s3: {}", fileUrl);
            String previewFileName = ImageUtils.BuildPreviewImageName(uuidName);
            MultipartFile previewMultipart = scaleImage(file, previewFileName, DEFAULT_FILE_EXTENSION);

            // upload preview
            fileStore.uploadFile(BucketName.IMAGE_BUCKET_NAME.getBucketName(), previewFileName, ImageUtils.ConvertMultiPartToFile(previewMultipart));
            String reviewFileUrl = fileStore.getUrl(BucketName.IMAGE_BUCKET_NAME.getBucketName(), previewFileName);

            List<RecognitionLabels> recognitionLabels = rekognitionService.searchLabels(file);

            // prepare image rekognition to store
            Set<ImageLabel> imageLabelSet = new HashSet<>();

            recognitionLabels.stream().forEach(i -> {
                ImageLabel imageLabel = ImageLabel.builder()
                        .name(i.getName().toLowerCase())
                        .confidence(i.getConfidence())
                        .build();
                imageLabelSet.add(imageLabel);
            });

            Image image = Image.builder()
                    .title(title)
                    .imagePath(fileUrl)
                    .imageFileName(fileName)
                    .previewImagePath(reviewFileUrl)
                    .labels(imageLabelSet)
                    .build();
            repository.save(image);

            log.debug("preview image saved, id:{}", image.getId());
            return ImageMapper.INSTANCE.toImageResponse(image);

        } catch (IOException e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public List<ImageResponse> getAll(String filter) {
        List<ImageResponse> result = new ArrayList<>();

        repository.findAll()
                .forEach(i -> {
                    result.add(ImageMapper.INSTANCE.toImageResponse(i));
                });
        return result;
    }

    @Override
    public List<ImageResponse> search(SearchQuery searchQuery) {
        Specification<Image> spec = SpecificationUtil.bySearchQuery(searchQuery, Image.class);
        PageRequest pageRequest = getPageRequest(searchQuery);

        Page<Image> all = repository.findAll(spec, pageRequest);

        List<ImageResponse> imageResponse = all.getContent().stream().distinct()
                .map(ImageMapper.INSTANCE::toImageResponse)
                .collect(Collectors.toList());

        log.info("Found {} images", imageResponse.size());
        return imageResponse;
    }

    @Override
    public ImageInformation getById(Long id) {
        log.info("Get image by id:{}", id);
        Image image = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id ", id));

        ImageResponse imageResponse = ImageMapper.INSTANCE.toImageResponse(image);
        List<RecognitionResponse> rekognitionList = new ArrayList();

        image.getLabels().stream()
                .forEach(i -> rekognitionList.add(RecognitionResponse.builder()
                        .name(i.getName())
                        .confidence(i.getConfidence())
                        .build()));

        return  new ImageInformation(imageResponse,rekognitionList );
    }

    private PageRequest getPageRequest(SearchQuery searchQuery) {

        int pageNumber = searchQuery.getPageNumber();
        int pageSize = searchQuery.getPageSize();

        List<Sort.Order> orders = new ArrayList<>();
        List<String> ascProps = searchQuery.getSortOrder().getAscendingOrder();

        if (ascProps != null && !ascProps.isEmpty()) {
            for (String prop : ascProps) {
                orders.add(Sort.Order.asc(prop));
            }
        }

        List<String> descProps = searchQuery.getSortOrder().getDescendingOrder();
        if (descProps != null && !descProps.isEmpty()) {
            for (String prop : descProps) {
                orders.add(Sort.Order.desc(prop));
            }
        }

        Sort sort = Sort.by(orders);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private MultipartFile scaleImage(MultipartFile multipartFile, String fileName, String format) {

        try {
            BufferedImage previewBufferedImage = ImageIO.read(multipartFile.getInputStream());

            int originalWidth = previewBufferedImage.getWidth();
            int originalHeight = previewBufferedImage.getHeight();
            log.debug("scale image, original weight:{}, height:{}", originalWidth, originalHeight);

            ResizeModel resizeModel = ImageUtils.BuildResizeModelByCurrentSize(originalWidth,
                    originalHeight, previewWidth, previewHeight);

            log.debug("started scaling image, resize to:{}", resizeModel);
            BufferedImage croppedBufferedImage = ImageUtils.CropImage(previewBufferedImage, resizeModel);

            //BufferedImage  Convert to  ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(croppedBufferedImage, format, out);
            //ByteArrayOutputStream  Convert to  byte[]
            byte[] imageByte = out.toByteArray();
            // Will  byte[]  Convert to  MultipartFile
            return new ConvertToMultipartFile(imageByte, fileName, multipartFile.getOriginalFilename(),
                    format, imageByte.length);
        } catch (Exception e) {
            log.error("some error" + e.getMessage());
            throw new ImageProcessingException("some error" + e.getMessage());
        }
    }
}
