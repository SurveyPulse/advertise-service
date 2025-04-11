package com.example.advertise_service.service;

import com.example.advertise_service.dto.request.AdvertisementRequest;
import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.entity.Advertisement;
import com.example.advertise_service.exception.AdvertisementExceptionType;
import com.example.advertise_service.repository.AdvertisementRepository;
import com.example.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FileStorageService fileStorageService;

    public Mono<AdvertisementResponse> getAdvertisement(String advertisementId) {
        log.info("조회 요청: 광고 ID {}", advertisementId);
        return advertisementRepository.findById(advertisementId)
                                      .switchIfEmpty(Mono.defer(() -> {
                                          log.error("조회 실패: 광고 ID {} not found", advertisementId);
                                          return Mono.error(new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND));
                                      }))
                                      .doOnNext(ad -> log.info("조회 성공: 광고 ID {} 조회 완료", advertisementId))
                                      .map(AdvertisementResponse::from);
    }

    public Flux<AdvertisementResponse> getAllAdvertisements() {
        log.info("전체 광고 목록 조회 요청");
        return advertisementRepository.findAll()
                                      .map(AdvertisementResponse::from)
                                      .doOnComplete(() -> log.info("전체 광고 목록 조회 완료"));
    }

    public Mono<AdvertisementResponse> createAdvertisement(AdvertisementRequest request, FilePart imageFile) {
        // request를 통해 동기적으로 엔티티 생성
        Advertisement advertisement = request.toEntity();

        Mono<Advertisement> saveOperation;
        if (imageFile != null) {
            // 파일 저장 후, 파일명이 반환되면 엔티티에 적용하여 저장
            saveOperation = fileStorageService.storeFile(imageFile)
                                              .flatMap(fileName -> {
                                                  // 엔티티 업데이트 (setter 또는 update 메서드 사용)
                                                  advertisement.update(advertisement.getTitle(), advertisement.getContent(), fileName,
                                                          advertisement.getStartDate(), advertisement.getEndDate());
                                                  return advertisementRepository.save(advertisement);
                                              });
        } else {
            saveOperation = advertisementRepository.save(advertisement);
        }

        return saveOperation
                .doOnNext(savedAd -> log.info("광고 생성 성공: 광고 ID {}", savedAd.getAdvertisementId()))
                .map(AdvertisementResponse::from);
    }

    public Mono<AdvertisementResponse> updateAdvertisement(String advertisementId, AdvertisementRequest request, FilePart imageFile) {
        log.info("광고 수정 요청: 광고 ID {}", advertisementId);
        return advertisementRepository.findById(advertisementId)
                                      .switchIfEmpty(Mono.defer(() -> {
                                          log.error("수정 실패: 광고 ID {} not found", advertisementId);
                                          return Mono.error(new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND));
                                      }))
                                      .flatMap(advertisement -> {
                                          // 이미지 파일이 첨부된 경우 파일 저장 처리
                                          Mono<String> imageMono;
                                          if (imageFile != null) {
                                              imageMono = fileStorageService.storeFile(imageFile)
                                                                            .subscribeOn(Schedulers.boundedElastic())
                                                                            .doOnNext(fileName -> log.info("수정용 이미지 파일 저장 완료, 새 파일명: {}", fileName));
                                          } else {
                                              log.info("이미지 파일 변경 없음 - 기존 이미지 유지");
                                              imageMono = Mono.just(advertisement.getImageUrl());
                                          }
                                          return imageMono.flatMap(newImageUrl -> {
                                              // 업데이트된 엔티티 적용 후 저장
                                              advertisement.update(
                                                      request.title(),
                                                      request.content(),
                                                      newImageUrl,
                                                      request.startDate(),
                                                      request.endDate()
                                              );
                                              return advertisementRepository.save(advertisement)
                                                                            .doOnNext(savedAd -> log.info("광고 수정 성공: 광고 ID {}", advertisementId))
                                                                            .map(AdvertisementResponse::from);
                                          });
                                      });
    }

    public Mono<Void> deleteAdvertisement(String advertisementId) {
        log.info("광고 삭제 요청: 광고 ID {}", advertisementId);
        return advertisementRepository.findById(advertisementId)
                                      .switchIfEmpty(Mono.defer(() -> {
                                          log.error("삭제 실패: 광고 ID {} not found", advertisementId);
                                          return Mono.error(new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND));
                                      }))
                                      .flatMap(advertisement -> {
                                          Mono<Void> deleteFileMono;
                                          if (advertisement.getImageUrl() != null && !advertisement.getImageUrl().isEmpty()) {
                                              deleteFileMono = Mono.fromRunnable(() -> fileStorageService.deleteFile(advertisement.getImageUrl()))
                                                                   .subscribeOn(Schedulers.boundedElastic())
                                                                   .doOnSuccess(unused -> log.info("S3 파일 삭제 완료, URL: {}", advertisement.getImageUrl()))
                                                                   .then();
                                          } else {
                                              deleteFileMono = Mono.empty();
                                          }
                                          return deleteFileMono.then(
                                                  advertisementRepository.delete(advertisement)
                                                                         .doOnSuccess(unused -> log.info("광고 삭제 성공: 광고 ID {}", advertisementId))
                                          ).then();
                                      });
    }
}
