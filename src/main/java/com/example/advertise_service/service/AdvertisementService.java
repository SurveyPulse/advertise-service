package com.example.advertise_service.service;

import com.example.advertise_service.dto.request.AdvertisementRequest;
import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.entity.Advertisement;
import com.example.advertise_service.exception.AdvertisementExceptionType;
import com.example.advertise_service.repository.AdvertisementRepository;
import com.example.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public AdvertisementResponse getAdvertisement(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                                      .orElseThrow(() -> new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementResponse.from(advertisement);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementResponse> getAllAdvertisements() {
        return advertisementRepository.findAll()
                                      .stream()
                                      .map(AdvertisementResponse::from)
                                      .collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementResponse createAdvertisement(AdvertisementRequest request, MultipartFile imageFile) {
        Advertisement advertisement = request.toEntity();

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            // 파일이 저장된 URL (정적 리소스 매핑 "/uploads/**" 필요)
            String imageUrl = "/uploads/" + fileName;
            // 재생성: 기존 request.toEntity() 결과에서 이미지 URL을 덮어쓰는 방식
            advertisement = Advertisement.builder()
                                         .title(advertisement.getTitle())
                                         .content(advertisement.getContent())
                                         .imageUrl(imageUrl)
                                         .startDate(advertisement.getStartDate())
                                         .endDate(advertisement.getEndDate())
                                         .build();
        }
        Advertisement savedAd = advertisementRepository.save(advertisement);
        return AdvertisementResponse.from(savedAd);
    }

}
