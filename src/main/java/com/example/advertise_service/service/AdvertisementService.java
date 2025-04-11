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
        log.info("조회 요청: 광고 ID {}", advertisementId);
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                                                             .orElseThrow(() -> {
                                                                 log.error("조회 실패: 광고 ID {} not found", advertisementId);
                                                                 return new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND);
                                                             });
        log.info("조회 성공: 광고 ID {} 조회 완료", advertisementId);
        return AdvertisementResponse.from(advertisement);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementResponse> getAllAdvertisements() {
        log.info("전체 광고 목록 조회 요청");
        List<AdvertisementResponse> responses = advertisementRepository.findAll()
                                                                       .stream()
                                                                       .map(AdvertisementResponse::from)
                                                                       .collect(Collectors.toList());
        log.info("전체 광고 목록 조회 완료, 조회 건수: {}", responses.size());
        return responses;
    }

    @Transactional
    public AdvertisementResponse createAdvertisement(AdvertisementRequest request, MultipartFile imageFile) {
        log.info("광고 생성 요청 시작");
        Advertisement advertisement = request.toEntity();

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            String imageUrl = "/uploads/" + fileName;
            log.info("이미지 파일 저장 완료, 파일명: {}", fileName);
            advertisement = Advertisement.builder()
                                         .title(advertisement.getTitle())
                                         .content(advertisement.getContent())
                                         .imageUrl(imageUrl)
                                         .startDate(advertisement.getStartDate())
                                         .endDate(advertisement.getEndDate())
                                         .build();
        } else {
            log.info("이미지 파일이 첨부되지 않았습니다.");
        }

        Advertisement savedAd = advertisementRepository.save(advertisement);
        log.info("광고 생성 성공: 광고 ID {}", savedAd.getAdvertisementId());
        return AdvertisementResponse.from(savedAd);
    }

    @Transactional
    public AdvertisementResponse updateAdvertisement(Long advertisementId, AdvertisementRequest request, MultipartFile imageFile) {
        log.info("광고 수정 요청: 광고 ID {}", advertisementId);
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                                                             .orElseThrow(() -> {
                                                                 log.error("수정 실패: 광고 ID {} not found", advertisementId);
                                                                 return new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND);
                                                             });

        // 기존 이미지 URL 기본값 설정
        String imageUrl = advertisement.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            imageUrl = "/uploads/" + fileName;
            log.info("수정용 이미지 파일 저장 완료, 새 파일명: {}", fileName);
        } else {
            log.info("이미지 파일 변경 없음 - 기존 이미지 유지");
        }

        advertisement.update(request.title(), request.content(), imageUrl, request.startDate(), request.endDate());
        log.info("광고 수정 성공: 광고 ID {}", advertisementId);
        return AdvertisementResponse.from(advertisement);
    }

    @Transactional
    public void deleteAdvertisement(Long advertisementId) {
        log.info("광고 삭제 요청: 광고 ID {}", advertisementId);
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                                                             .orElseThrow(() -> {
                                                                 log.error("삭제 실패: 광고 ID {} not found", advertisementId);
                                                                 return new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND);
                                                             });
        advertisementRepository.delete(advertisement);
        log.info("광고 삭제 성공: 광고 ID {}", advertisementId);
    }
}
