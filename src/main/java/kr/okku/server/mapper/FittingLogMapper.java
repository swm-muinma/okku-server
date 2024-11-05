package kr.okku.server.mapper;
import kr.okku.server.adapters.persistence.repository.fitting.FittingEntity;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogEntity;
import kr.okku.server.adapters.persistence.repository.pick.PickEntity;
import kr.okku.server.domain.FittingDomain;
import kr.okku.server.domain.FittingLogDomain;
import kr.okku.server.domain.PickDomain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                .itemPk(fittingLogEntity.getItemPk())
                .itemPlatform(fittingLogEntity.getItemPlatform())
                .callTime(fittingLogEntity.getCreatedAt().toString())
                .doneTime(fittingLogEntity.getUpdatedAt().toString())
                .build();
    }

    public static FittingLogEntity toEntity(FittingLogDomain fittingLogDomain) {
        if (fittingLogDomain == null) {
            return null;
        }
        FittingLogEntity fittingLogEntity = new FittingLogEntity();
        fittingLogEntity.setId(fittingLogDomain.getId());
        fittingLogEntity.setUserId(fittingLogDomain.getUserId());
        fittingLogEntity.setUserName(fittingLogDomain.getUserName());
        fittingLogEntity.setResponseImage(fittingLogDomain.getResponseImage());
        fittingLogEntity.setRequestItemImage(fittingLogDomain.getRequestItemImage());
        fittingLogEntity.setRequestItemUrl(fittingLogDomain.getRequestItemUrl());
        fittingLogEntity.setRequestUserImage(fittingLogDomain.getRequestUserImage());
        fittingLogEntity.setResponseMessage(fittingLogDomain.getResponseMessage());
        fittingLogEntity.setFittingResultId(fittingLogDomain.getFittingResultId());
        fittingLogEntity.setItemPk(fittingLogDomain.getItemPk());
        fittingLogEntity.setItemPlatform(fittingLogEntity.getItemPlatform());
        if(fittingLogDomain.getCallTime()!=null){
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

            try {
                // Parse the string to a Date object
                Date createdAt = formatter.parse(fittingLogDomain.getCallTime());
                fittingLogEntity.setCreatedAt(createdAt);
                // Now you can assign `createdAt` to your object
                // fittingLogDomain.setCreatedAt(createdAt); // example of setting it in your object
            } catch (ParseException e) {
                System.err.println("Failed to parse date: " + e.getMessage());
            }
        }
        return fittingLogEntity;
    }

}