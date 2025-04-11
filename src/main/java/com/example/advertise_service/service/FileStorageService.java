package com.example.advertise_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("업로드 파일을 저장할 디렉터리를 생성하지 못했습니다.", e);
        }
    }

    public String storeFile(MultipartFile file) {
        // UUID를 붙여서 고유 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        if (fileName.contains("..")) {
            throw new RuntimeException("잘못된 파일명: " + fileName);
        }

        try {
            Path targetLocation = this.storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + fileName, ex);
        }
    }
}
