package com.example.advertise_service.controller;

import com.example.advertise_service.dto.request.AdvertisementRequest;
import com.example.advertise_service.dto.response.AdvertisementResponse;
import com.example.advertise_service.dto.response.AdvertisementSummaryResponse;
import com.example.advertise_service.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping("/{advertisementId}")
    public Mono<ResponseEntity<AdvertisementResponse>> getAdvertisement(@PathVariable String advertisementId) {
        return advertisementService.getAdvertisement(advertisementId)
                                   .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<AdvertisementResponse> getAllAdvertisements() {
        return advertisementService.getAllAdvertisements();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<AdvertisementResponse>> createAdvertisement(
            @RequestPart("advertisement") AdvertisementRequest request,
            @RequestPart(value = "imageFile", required = false) FilePart imageFile) {
        return advertisementService.createAdvertisement(request, imageFile)
                                   .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping(value = "/{advertisementId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<AdvertisementResponse>> updateAdvertisement(
            @PathVariable String advertisementId,
            @RequestPart("advertisement") AdvertisementRequest request,
            @RequestPart(value = "imageFile", required = false) FilePart imageFile) {
        return advertisementService.updateAdvertisement(advertisementId, request, imageFile)
                                   .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{advertisementId}")
    public Mono<ResponseEntity<Void>> deleteAdvertisement(@PathVariable String advertisementId) {
        return advertisementService.deleteAdvertisement(advertisementId)
                                   .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/random")
    public Mono<ResponseEntity<List<AdvertisementSummaryResponse>>> getRandomAdvertisements(
            @RequestParam(name = "count", defaultValue = "1") int count) {
        return advertisementService.selectAdvertisementsWithWeight(count)
                                   .map(ResponseEntity::ok);
    }
}
