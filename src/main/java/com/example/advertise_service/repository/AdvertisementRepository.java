package com.example.advertise_service.repository;

import com.example.advertise_service.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("SELECT a FROM Advertisement a WHERE a.startDate <= :current AND a.endDate >= :current")
    List<Advertisement> findActiveAdvertisements(@Param("current") LocalDateTime current);
}
