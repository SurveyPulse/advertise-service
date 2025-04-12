package com.example.advertise_service.dto.request;

import com.example.advertise_service.entity.Advertisement;

import java.time.LocalDateTime;

public record AdvertisementRequest(
        String title,
        String content,
        String targetUrl,
        LocalDateTime startDate,
        LocalDateTime endDate,
        double weight
) {
    public Advertisement toEntity() {
        return Advertisement.builder()
                            .title(title)
                            .content(content)
                            .targetUrl(targetUrl)
                            .startDate(startDate)
                            .endDate(endDate)
                            .weight(weight)
                            .build();
    }
}
