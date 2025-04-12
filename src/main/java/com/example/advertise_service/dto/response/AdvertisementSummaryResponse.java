package com.example.advertise_service.dto.response;

import com.example.advertise_service.entity.Advertisement;

public record AdvertisementSummaryResponse(
        Long advertisementId,
        String imageUrl,
        String targetUrl
) {
    public static AdvertisementSummaryResponse from(Advertisement advertisement) {
        return new AdvertisementSummaryResponse(
                advertisement.getAdvertisementId(),
                advertisement.getImageUrl(),
                advertisement.getTargetUrl()
        );
    }
}
