package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogEntity;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.FittingLogDomain;
import kr.okku.server.domain.PickDomain;

public class FittingLogMapper {

    public static FittingLogDomain toDomain(FittingLogEntity fittingLogEntity) {
        if (fittingLogEntity == null) {
            return null;
        }
        return FittingLogDomain.builder()
                .id(fittingLogEntity.getId())
                .requestItemImage(fittingLogEntity.getRequestItemImage())
                .requestItemUrl(fittingLogEntity.getRequestItemUrl())
                .requestUserImage(fittingLogEntity.getRequestUserImage())
                .responseImage(fittingLogEntity.getResponseImage())
                .responseMessage(fittingLogEntity.getResponseMessage())
                .userId(fittingLogEntity.getUserId())
                .userName(fittingLogEntity.getUserName())
                .fittingResultId(fittingLogEntity.getFittingResultId())
                .build();
    }

    public static FittingLogEntity toEntity(FittingLogDomain fittingLogDomain) {
        if (fittingLogDomain == null) {
            return null;
        }
        FittingLogEntity fittingLogEntity = new FittingLogEntity();
        fittingLogEntity.setUserId(fittingLogDomain.getUserId());
        fittingLogEntity.setUserName(fittingLogDomain.getUserName());
        fittingLogEntity.setResponseImage(fittingLogDomain.getResponseImage());
        fittingLogEntity.setRequestItemImage(fittingLogDomain.getRequestItemImage());
        fittingLogEntity.setRequestItemUrl(fittingLogDomain.getRequestItemUrl());
        fittingLogEntity.setRequestUserImage(fittingLogDomain.getRequestUserImage());
        fittingLogEntity.setResponseMessage(fittingLogDomain.getResponseMessage());
        fittingLogEntity.setFittingResultId(fittingLogDomain.getFittingResultId());
        return fittingLogEntity;
    }

}