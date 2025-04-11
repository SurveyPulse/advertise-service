package com.example.advertise_service.controller;

import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping("/{advertisementId}")
    public ResponseEntity<AdvertisementResponse> getAdvertisement(@PathVariable Long advertisementId) {
        AdvertisementResponse advertisementResponse = advertisementService.getAdvertisement(advertisementId);
        return ResponseEntity.ok(advertisementResponse);
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements() {
        List<AdvertisementResponse> responses = advertisementService.getAllAdvertisements();
        return ResponseEntity.ok(responses);
    }
}
