package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogEntity;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.FittingLogDomain;
import kr.okku.server.domain.UserDomain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FittingMapper {

    public static FittingDomain toDomain(FittingEntity fittingEntity) {
        if (fittingEntity == null) {
            return null;
        }
        return FittingDomain.builder()
                .id(fittingEntity.getId())
                .clothesPlatform(fittingEntity.getClothesPlatform())
                .userPk(fittingEntity.getUserPk())
                .imgUrl(fittingEntity.getImgUrl())
                .clothesPk(fittingEntity.getClothesPk())
                .status(fittingEntity.getStatus())
                .createdAt(fittingEntity.getCreatedAt())
                .build();
    }

    public static FittingEntity toEntity(FittingDomain fittingDomain) {
        if (fittingDomain == null) {
            return null;
        }
        FittingEntity fittingEntity = new FittingEntity();
        fittingEntity.setId(fittingDomain.getId());
        fittingEntity.setStatus(fittingEntity.getStatus());
        fittingEntity.setCreatedAt(fittingDomain.getCreatedAt());
        fittingEntity.setImgUrl(fittingEntity.getImgUrl());
        fittingEntity.setClothesPk(fittingEntity.getClothesPk());
        fittingEntity.setClothesPlatform(fittingEntity.getClothesPlatform());
        fittingEntity.setUserPk(fittingEntity.getUserPk());
        return fittingEntity;
    }
}