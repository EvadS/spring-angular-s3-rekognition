package com.se.sample.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.se.sample.service.FileStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class FileStoreImpl implements FileStore {
    private final AmazonS3 amazonS3;

    @Override
    public void uploadFile(String bucketName, String fileName, File file) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
            file.delete();
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public String getUrl(String bucketName, String name) {
        return amazonS3.getUrl(bucketName, name).toString();
    }

    @Override
    public byte[] download(String path, String key) {
        try {
            S3Object object = amazonS3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }
}
