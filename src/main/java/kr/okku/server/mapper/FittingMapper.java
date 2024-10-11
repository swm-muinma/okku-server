package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.user.UserEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.UserDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
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
                .build();
    }
}