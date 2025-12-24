package com.info.ecommerce.modules.store.repository;

import com.info.ecommerce.modules.store.entity.PopupAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PopupAdRepository extends JpaRepository<PopupAd, Long> {

    List<PopupAd> findByEnabledTrue();

    @Query("SELECT p FROM PopupAd p WHERE p.enabled = true " +
           "AND (p.startTime IS NULL OR p.startTime <= :now) " +
           "AND (p.endTime IS NULL OR p.endTime >= :now)")
    List<PopupAd> findActiveAds(LocalDateTime now);
}
