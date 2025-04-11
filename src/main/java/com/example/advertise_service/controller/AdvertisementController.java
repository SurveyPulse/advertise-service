package com.example.advertise_service.controller;

import com.example.advertise_service.dto.request.AdvertisementRequest;
import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping
    public ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestPart("advertisement") AdvertisementRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        AdvertisementResponse response = advertisementService.createAdvertisement(request, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
