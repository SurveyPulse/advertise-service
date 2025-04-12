package com.example.advertise_service.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "advertisements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertisement {

    @Id
    private String advertisementId;  // MongoDB ObjectId 혹은 문자열

    private String title;

    private String content;

    private String imageUrl;

    private String targetUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double weight;

    @Builder
    public Advertisement(String title, String content, String imageUrl,
                         String targetUrl, LocalDateTime startDate, LocalDateTime endDate, double weight) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weight = weight;
    }

    public void update(String title, String content, String imageUrl, String targetUrl,
                       LocalDateTime startDate, LocalDateTime endDate, double weight) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weight = weight;
    }
}
