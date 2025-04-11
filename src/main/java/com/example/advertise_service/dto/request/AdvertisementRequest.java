package com.example.advertise_service.dto.request;

import com.example.advertise_service.entity.Advertisement;

import java.time.LocalDateTime;

public record AdvertisementRequest(
        String title,
        String content,
        String imageUrl,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public Advertisement toEntity() {
        return Advertisement.builder()
                            .title(title)
                            .content(content)
                            .imageUrl(imageUrl)
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
    }
}
