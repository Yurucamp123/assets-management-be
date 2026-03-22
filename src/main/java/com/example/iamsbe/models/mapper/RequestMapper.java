package com.example.iamsbe.models.mapper;

import com.example.iamsbe.models.entities.Request;
import com.example.iamsbe.models.requests.BorrowRequest;
import com.example.iamsbe.models.responses.RequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    // Chuyển từ DTO sang Entity để lưu DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Sẽ set thủ công trong Service
    @Mapping(target = "asset", ignore = true) // Sẽ set thủ công trong Service
    Request toEntity(BorrowRequest request);

    // Chuyển từ Entity sang Response để trả về Client
    @Mapping(source = "user.username", target = "username") // Lấy từ User entity
    @Mapping(source = "asset.assetName", target = "assetName")  // Lấy từ Asset entity
    @Mapping(source = "asset.assetTag", target = "assetTag") // Lấy từ Asset entity
    RequestResponse toResponse(Request request);

    void updateEntity(@MappingTarget Request request, BorrowRequest borrowRequest);
}