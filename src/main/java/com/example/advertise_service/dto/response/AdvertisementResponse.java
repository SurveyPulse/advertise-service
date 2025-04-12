package com.example.advertise_service.dto.response;

import com.example.advertise_service.entity.Advertisement;

import java.time.LocalDateTime;

public record AdvertisementResponse(
        String advertisementId,
        String title,
        String content,
        String imageUrl,
        String targetUrl,
        LocalDateTime startDate,
        LocalDateTime endDate,
        double weight
) {
    public static AdvertisementResponse from(Advertisement advertisement) {
        return new AdvertisementResponse(
                advertisement.getAdvertisementId(),
                advertisement.getTitle(),
                advertisement.getContent(),
                advertisement.getImageUrl(),
                advertisement.getTargetUrl(),
                advertisement.getStartDate(),
                advertisement.getEndDate(),
                advertisement.getWeight()
        );
    }
}
