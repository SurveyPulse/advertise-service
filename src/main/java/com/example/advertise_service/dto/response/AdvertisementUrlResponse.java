package com.example.advertise_service.dto.response;

import com.example.advertise_service.entity.Advertisement;

public record AdvertisementUrlResponse(
        String imageUrl
) {
    public static AdvertisementUrlResponse from(Advertisement advertisement) {
        return new AdvertisementUrlResponse(
                advertisement.getImageUrl()
        );
    }
}
