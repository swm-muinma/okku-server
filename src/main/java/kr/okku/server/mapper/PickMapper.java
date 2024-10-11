package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.adapters.persistence.repository.pick.PlatformEntity;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.PlatformDomain;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import kr.okku.server.dto.controller.pick.PickItemResponseDto;
import kr.okku.server.dto.controller.pick.PickPlatformResponseDto;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Optional;

public class PickMapper {

    // Entity -> Domain
    public static PickDomain toDomain(PickEntity pickEntity) {
        if (pickEntity == null) {
            return null;
        }
        return PickDomain.builder()
                .id(pickEntity.getId())
                .url(pickEntity.getUrl())
                .userId(pickEntity.getUserId())
                .name(pickEntity.getName())
                .price(pickEntity.getPrice())
                .image(pickEntity.getImage())
                .platform(toDomain(pickEntity.getPlatform())) // PlatformEntity to PlatformDomain
                .pk(pickEntity.getPk())
                .fittingList(pickEntity.getFittingList())
                .brand(Optional.ofNullable(pickEntity.getBrand()).orElse(""))
                .category(Optional.ofNullable(pickEntity.getCategory()).orElse(""))
                .fittingPart(Optional.ofNullable(pickEntity.getFittingPart()).orElse("upper_body"))
                .build();
    }

    // Domain -> Entity
    public static PickEntity toEntity(PickDomain pickDomain) {
        if (pickDomain == null) {
            return null;
        }
        PickEntity pickEntity = new PickEntity();
        pickEntity.setId(pickDomain.getId());
        pickEntity.setUrl(pickDomain.getUrl());
        pickEntity.setUserId(pickDomain.getUserId());
        pickEntity.setName(pickDomain.getName());
        pickEntity.setPrice(pickDomain.getPrice());
        pickEntity.setImage(pickDomain.getImage());
        pickEntity.setPlatform(toEntity(pickDomain.getPlatform())); // PlatformDomain to PlatformEntity
        pickEntity.setPk(pickDomain.getPk());
        pickEntity.setFittingList(pickDomain.getFittingList());
        pickEntity.setBrand(pickDomain.getBrand());
        pickEntity.setCategory(pickDomain.getCategory());
        return pickEntity;
    }

    // PlatformEntity -> PlatformDomain
    public static PlatformDomain toDomain(PlatformEntity platformEntity) {
        if (platformEntity == null) {
            return null;
        }
        return PlatformDomain.builder()
                .name(platformEntity.getName())
                .image(platformEntity.getImage())
                .url(platformEntity.getUrl())
                .build();
    }

    // PlatformDomain -> PlatformEntity
    private static PlatformEntity toEntity(PlatformDomain platformDomain) {
        if (platformDomain == null) {
            return null;
        }
        PlatformEntity platformEntity = new PlatformEntity();
        platformEntity.setName(platformDomain.getName());
        platformEntity.setImage(platformDomain.getImage());
        platformEntity.setUrl(platformDomain.getUrl());
        return platformEntity;
    }

    public static PickItemResponseDto convertToPickDTO(PickDomain pickDomain) {
        PickItemResponseDto dto = new PickItemResponseDto();
        dto.setId(pickDomain.getId());
        dto.setName(pickDomain.getName());
        dto.setPrice(pickDomain.getPrice());
        dto.setImage(pickDomain.getImage());
        dto.setUrl(pickDomain.getUrl());

        PickPlatformResponseDto platformDTO = new PickPlatformResponseDto();
        platformDTO.setName(pickDomain.getPlatform().getName());
        platformDTO.setImage(pickDomain.getPlatform().getImage());
        platformDTO.setUrl(pickDomain.getPlatform().getUrl());

        dto.setPlatform(platformDTO);
        return dto;
    }

    public static PageInfoResponseDto convertToPageInfoDTO(Page<PickDomain> page) {
        PageInfoResponseDto pageInfo = new PageInfoResponseDto();
        pageInfo.setTotalDataCnt((int) page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setLastPage(page.isLast());
        pageInfo.setFirstPage(page.isFirst());
        pageInfo.setRequestPage(page.getNumber() + 1);
        pageInfo.setRequestSize(page.getSize());
        return pageInfo;
    }
}
