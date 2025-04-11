package com.example.advertise_service.dto.response;

import com.example.advertise_service.entity.Advertisement;

import java.time.LocalDateTime;

public record AdvertisementResponse(
        String advertisementId,
        String title,
        String content,
        String imageUrl,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public static AdvertisementResponse from(Advertisement advertisement) {
        return new AdvertisementResponse(
                advertisement.getAdvertisementId(),
                advertisement.getTitle(),
                advertisement.getContent(),
                advertisement.getImageUrl(),
                advertisement.getStartDate(),
                advertisement.getEndDate()
        );
    }
}
