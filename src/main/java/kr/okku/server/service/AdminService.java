package kr.okku.server.service;

import kr.okku.server.adapters.persistence.*;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.dto.controller.admin.FiittingListResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final PickPersistenceAdapter pickPersistenceAdapter;
    private final CartPersistenceAdapter cartPersistenceAdapter;
    private final ScraperAdapter scraperAdapter;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final ItemPersistenceAdapter itemPersistenceAdapter;
    private final FittingPersistenceAdapter fittingPersistenceAdapter;

    private final FittingLogPersistenceAdapter fittingLogPersistenceAdapter;

    private final Utils utils;

    @Autowired
    public AdminService(PickPersistenceAdapter pickPersistenceAdapter, CartPersistenceAdapter cartPersistenceAdapter,
                        ScraperAdapter scraperAdapter, UserPersistenceAdapter userPersistenceAdapter, ItemPersistenceAdapter itemPersistenceAdapter, FittingPersistenceAdapter fittingPersistenceAdapter, FittingLogPersistenceAdapter fittingLogPersistenceAdapter, Utils utils
                     ) {
        this.pickPersistenceAdapter = pickPersistenceAdapter;
        this.cartPersistenceAdapter = cartPersistenceAdapter;
        this.scraperAdapter = scraperAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.itemPersistenceAdapter = itemPersistenceAdapter;
        this.fittingPersistenceAdapter = fittingPersistenceAdapter;
        this.fittingLogPersistenceAdapter = fittingLogPersistenceAdapter;
        this.utils = utils;
    }

    public List<FiittingListResponseDto> getFiittingList(String userId) {
        List<FittingLogDomain> fittingLogDomains = fittingLogPersistenceAdapter.findAll();
        return fittingLogDomains.stream()
                .map(fittingLog -> {
                    // responseImage가 null인 경우 fittingPersistenceAdapter로 값을 채워준다.
                    if (fittingLog.getResponseImage() == null) {
                        FittingDomain fittingDomain = fittingPersistenceAdapter.findById(fittingLog.getFittingResultId()).orElse(null);
                        if(fittingDomain==null){
                            throw new ErrorDomain(ErrorCode.LOG_NOT_FOUND,null);
                        }
                        if (fittingDomain != null) { // fittingDomain이 존재하는 경우에만 값을 채움
                            fittingLog.setResponseImage(fittingDomain.getImgUrl());
                            fittingLog.setResponseMessage(fittingDomain.getStatus().getValue());
                        }
                    }
                    fittingLogPersistenceAdapter.save(fittingLog);
                    // FittingLogDomain을 FiittingListResponseDto로 변환
                    return FiittingListResponseDto.builder()
                            .userId(fittingLog.getUserId())
                            .userName(fittingLog.getUserName())
                            .requestUserImage(fittingLog.getRequestUserImage())
                            .requestItemImage(fittingLog.getRequestItemImage())
                            .requestItemUrl(fittingLog.getRequestItemUrl())
                            .responseImage(fittingLog.getResponseImage())
                            .responseMessage(fittingLog.getResponseMessage())
                            .callTime(fittingLog.getCallTime())
                            .doneTime(fittingLog.getDoneTime())
                            .build();
                })
                .collect(Collectors.toList());
    }



}
