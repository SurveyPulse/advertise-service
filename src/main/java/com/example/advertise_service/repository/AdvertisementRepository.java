package com.example.advertise_service.repository;

import com.example.advertise_service.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

}
