package com.example.advertise_service.entity;

import com.example.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "advertisements")
public class Advertisement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long advertisementId;

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
