package com.example.advertise_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String storeFile(MultipartFile file) {
        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException ex) {
            throw new RuntimeException("S3에 파일 저장에 실패하였습니다: " + fileName, ex);
        }

        // CloudFront를 사용하는 경우, 여기서 도메인을 변경할 수 있습니다.
        // 예: return "https://d111111abcdef8.cloudfront.net/" + fileName;
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String encodedFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String decodedFileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
        amazonS3.deleteObject(bucketName, decodedFileName);
    }

}
