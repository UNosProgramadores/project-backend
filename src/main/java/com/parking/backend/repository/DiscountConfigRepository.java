package com.parking.backend.repository;

import com.parking.backend.entity.DiscountConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountConfigRepository extends JpaRepository<DiscountConfig, Long> {
}
