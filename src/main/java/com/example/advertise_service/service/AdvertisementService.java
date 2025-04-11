package com.example.advertise_service.service;

import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.entity.Advertisement;
import com.example.advertise_service.exception.AdvertisementExceptionType;
import com.example.advertise_service.repository.AdvertisementRepository;
import com.example.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    @Transactional(readOnly = true)
    public AdvertisementResponse getAdvertisement(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                                      .orElseThrow(() -> new NotFoundException(AdvertisementExceptionType.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementResponse.from(advertisement);
    }

}
