package com.example.iamsbe.models.mapper;

import com.example.iamsbe.models.entities.Asset;
import com.example.iamsbe.models.requests.AssetRequest;
import com.example.iamsbe.models.responses.AssetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    Asset toEntity(AssetRequest request);
    AssetResponse toResponse(Asset asset);
    void updateEntity(@MappingTarget Asset asset, AssetRequest request);
}