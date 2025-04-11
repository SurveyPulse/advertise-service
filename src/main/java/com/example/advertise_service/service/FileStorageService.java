package com.example.advertise_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
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

    public Mono<String> storeFile(FilePart filePart) {
        // 고유한 파일명 생성
        String originalFilename = filePart.filename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);

        // FilePart.transferTo()를 사용하여 임시 파일에 저장
        return filePart.transferTo(tempFile)
                       .then(Mono.fromCallable(() -> {
                           try (FileInputStream inputStream = new FileInputStream(tempFile)) {
                               ObjectMetadata metadata = new ObjectMetadata();
                               metadata.setContentLength(tempFile.length());
                               amazonS3.putObject(bucketName, fileName, inputStream, metadata);
                           } catch (IOException ex) {
                               throw new RuntimeException("S3에 파일 저장 실패: " + fileName, ex);
                           }
                           // 임시 파일 삭제
                           tempFile.delete();
                           // CloudFront 등을 사용하는 경우 도메인 변경
                           return amazonS3.getUrl(bucketName, fileName).toString();
                       }).subscribeOn(Schedulers.boundedElastic()));
    }

    public void deleteFile(String fileUrl) {
        String encodedFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String decodedFileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
        amazonS3.deleteObject(bucketName, decodedFileName);
    }
}
