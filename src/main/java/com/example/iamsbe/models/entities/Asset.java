package com.example.iamsbe.models.entities;

import com.example.iamsbe.models.enums.AssetCategory;
import com.example.iamsbe.models.enums.AssetStatus;
import com.example.iamsbe.utils.AssetCategoryConverter;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "assets")
@Data
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assetName;
    private String assetTag;
    @Column(nullable = false)
    @Convert(converter = AssetCategoryConverter.class)
    private AssetCategory category;
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
}