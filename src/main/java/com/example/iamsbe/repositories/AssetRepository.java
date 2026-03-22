package com.example.iamsbe.repositories;

import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsByAssetTag(String assetTag);
    Page<Asset> findAllByStatus(AssetStatus status, Pageable pageable);
    long countByStatus(AssetStatus status);
}
