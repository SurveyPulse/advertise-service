package com.example.advertise_service.service;

import com.example.advertise_service.dto.request.AdvertisementRequest;
import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.dto.response.AdvertisementSummaryResponse;
import com.example.advertise_service.entity.Advertisement;
import com.example.advertise_service.exception.AdvertisementExceptionType;
import com.example.advertise_service.repository.AdvertisementRepository;
import com.example.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FileStorageService fileStorageService;
    private final Clock clock;

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
            String imageUrl = fileName;
            log.info("이미지 파일 저장 완료, 파일명: {}", fileName);
            advertisement = Advertisement.builder()
                                         .title(advertisement.getTitle())
                                         .content(advertisement.getContent())
                                         .imageUrl(imageUrl)
                                         .targetUrl(advertisement.getTargetUrl())
                                         .startDate(advertisement.getStartDate())
                                         .endDate(advertisement.getEndDate())
                                         .weight(advertisement.getWeight())
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
            imageUrl = fileName;
            log.info("수정용 이미지 파일 저장 완료, 새 파일명: {}", fileName);
        } else {
            log.info("이미지 파일 변경 없음 - 기존 이미지 유지");
        }

        advertisement.update(request.title(), request.content(), imageUrl, request.targetUrl(), request.startDate(), request.endDate(), request.weight());
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

        String imageUrl = advertisement.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                fileStorageService.deleteFile(imageUrl);
                log.info("S3 파일 삭제 완료, URL: {}", imageUrl);
            } catch (Exception e) {
                log.error("S3 파일 삭제 실패, URL: {}", imageUrl, e);
            }
        }

        advertisementRepository.delete(advertisement);
        log.info("광고 삭제 성공: 광고 ID {}", advertisementId);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementSummaryResponse> selectAdvertisementsWithWeight(int adCount) {
        LocalDateTime now = LocalDateTime.now(clock);
        // 현재 활성화된 광고들을 조회 (시작일 ≤ 현재 ≤ 종료일)
        List<Advertisement> activeAds = advertisementRepository
                .findActiveAdvertisements(now);

        if (activeAds.isEmpty()) {
            log.warn("활성 광고가 없습니다.");
            throw new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND);
        }

        // 요청한 광고 개수가 활성 광고 수보다 클 경우 활성 광고 모두 반환합니다.
        if (adCount >= activeAds.size()) {
            log.info("요청 광고 개수({})가 활성 광고 개수({})보다 많거나 같으므로 전체 광고 반환", adCount, activeAds.size());
            return activeAds.stream()
                            .map(AdvertisementSummaryResponse::from)
                            .collect(Collectors.toList());
        }

        // 가중치 기반 무작위 선택(중복 없이) 구현
        List<Advertisement> selectedAds = new ArrayList<>();
        List<Advertisement> mutableAds = new ArrayList<>(activeAds);

        for (int i = 0; i < adCount; i++) {
            Advertisement ad = weightedRandomSelection(mutableAds);
            selectedAds.add(ad);
            // 이미 선택된 광고는 목록에서 제거해 중복 선택 방지
            mutableAds.remove(ad);
        }

        log.info("가중치 기반 광고 {}개 선택 완료", selectedAds.size());
        return selectedAds.stream()
                          .map(AdvertisementSummaryResponse::from)
                          .collect(Collectors.toList());
    }


    private Advertisement weightedRandomSelection(List<Advertisement> ads) {
        // 전체 광고 가중치 합 구하기
        double totalWeight = ads.stream().mapToDouble(Advertisement::getWeight).sum();

        double random = Math.random() * totalWeight;

        // 순회하면서 난수가 포함되는 광고 선택
        for (Advertisement ad : ads) {
            random -= ad.getWeight();
            if (random <= 0) {
                return ad;
            }
        }
        // 보통 위 로직에서 이미 반환되지만, 혹시 모를 경우 마지막 광고 반환
        return ads.get(ads.size() - 1);
    }
}
