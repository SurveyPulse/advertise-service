package com.example.advertise_service.dto.request;

import java.time.LocalDateTime;

public record AdvertisementRequest(
        String title,
        String content,
        String imageUrl,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
