package com.example.advertise_service.repository;

import com.example.advertise_service.entity.Advertisement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AdvertisementRepository extends ReactiveMongoRepository<Advertisement, String> {
}
