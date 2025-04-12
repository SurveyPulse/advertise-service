package com.example.advertise_service.repository;

import com.example.advertise_service.entity.Advertisement;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface AdvertisementRepository extends ReactiveMongoRepository<Advertisement, String> {

    @Query("{ 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    Flux<Advertisement> findActiveAdvertisements(LocalDateTime current);

}
