package kr.okku.server.service;

import kr.okku.server.adapters.persistence.*;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.admin.FiittingListResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        String userRole = userPersistenceAdapter.getRole(userId).orElse(null);
        System.out.println(userRole);
        if(userRole==null || !userRole.equals("admin")){
            throw new ErrorDomain(ErrorCode.FORBIDDEN,new TraceId());
        }
        List<FittingLogDomain> fittingLogDomains = fittingLogPersistenceAdapter.findAll();
        return fittingLogDomains.stream()
                .map(fittingLog -> {
                    // responseImage가 null인 경우 fittingPersistenceAdapter로 값을 채워준다.
                    if (fittingLog.getResponseMessage().equals("not served")) {
                        FittingDomain fittingDomain = fittingPersistenceAdapter.findById(fittingLog.getFittingResultId()).orElse(null);
                        if (fittingDomain != null) { // fittingDomain이 존재하는 경우에만 값을 채움
                            fittingLog.setResponseImage(fittingDomain.getImgUrl());
                            fittingLog.setResponseMessage(fittingDomain.getStatus().getValue());
                        }
                        fittingLogPersistenceAdapter.save(fittingLog);
                    }

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

    public void getRetentionStats() {
        List<FittingLogDomain> fittingLogDomains = fittingLogPersistenceAdapter.findAll();
        System.out.println("Total logs: " + fittingLogDomains.size());

        Map<String, Boolean> userDoneWithin24HoursMap = new HashMap<>();
        Map<String, Boolean> userCalledAgainAfter24HoursMap = new HashMap<>();
        Map<String, Double> userDoneRatioWithin24HoursMap = new HashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        // Group logs by userId to analyze on a per-user basis
        Map<String, List<FittingLogDomain>> logsByUser = fittingLogDomains.stream()
                .collect(Collectors.groupingBy(FittingLogDomain::getUserId));

        for (Map.Entry<String, List<FittingLogDomain>> entry : logsByUser.entrySet()) {
            String userId = entry.getKey();
            List<FittingLogDomain> logs = entry.getValue();

            // Sort logs by callTime for each user
            logs.sort(Comparator.comparing(FittingLogDomain::getCallTime));

            boolean hasDoneWithin24Hours = false;
            boolean hasCalledAgainAfter24Hours = false;
            Date firstCallTime;
            int totalCallsWithin24Hours = 0;
            int doneCountWithin24Hours = 0;

            try {
                firstCallTime = formatter.parse(logs.get(0).getCallTime());
            } catch (ParseException e) {
                System.err.println("Failed to parse first callTime for user " + userId);
                continue;
            }

            for (FittingLogDomain log : logs) {
                Date callTime;
                try {
                    callTime = formatter.parse(log.getCallTime());
                } catch (ParseException e) {
                    System.err.println("Failed to parse callTime for user " + userId);
                    continue;
                }

                long timeDifferenceInMillis = callTime.getTime() - firstCallTime.getTime();
                long hoursDifference = timeDifferenceInMillis / (1000 * 60 * 60);

                if (hoursDifference <= 24) {
                    totalCallsWithin24Hours++;
                    if ("done".equals(log.getResponseMessage())) {
                        doneCountWithin24Hours++;
                        hasDoneWithin24Hours = true;
                    }
                }
                if (hoursDifference > 24) {
                    hasCalledAgainAfter24Hours = true;
                    break; // No need to check further logs
                }
            }

            // Only process users who have attempted 5 or more fittings within the first 24 hours
            if (totalCallsWithin24Hours >= 5) {
                // Calculate done ratio within 24 hours and update maps
                double doneRatioWithin24Hours = totalCallsWithin24Hours > 0 ? (double) doneCountWithin24Hours / totalCallsWithin24Hours : 0;
                userDoneRatioWithin24HoursMap.put(userId, doneRatioWithin24Hours);
                userDoneWithin24HoursMap.put(userId, hasDoneWithin24Hours);
                userCalledAgainAfter24HoursMap.put(userId, hasCalledAgainAfter24Hours);
            }
        }

        long totalUsers = userDoneWithin24HoursMap.size();
        long usersWithDoneWithin24Hours = userDoneWithin24HoursMap.values().stream().filter(Boolean::booleanValue).count();
        long usersWithDoneAndCallAfter24Hours = userDoneWithin24HoursMap.entrySet().stream()
                .filter(entry -> entry.getValue() && userCalledAgainAfter24HoursMap.getOrDefault(entry.getKey(), false))
                .count();
        long usersWithoutDoneWithin24Hours = totalUsers - usersWithDoneWithin24Hours;
        long usersWithoutDoneAndCallAfter24Hours = userDoneWithin24HoursMap.entrySet().stream()
                .filter(entry -> !entry.getValue() && userCalledAgainAfter24HoursMap.getOrDefault(entry.getKey(), false))
                .count();

        // Additional requested metrics
        long usersWithDoneRatioAbove70 = userDoneRatioWithin24HoursMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.70) // Change threshold to 0.70
                .count();
        long usersWithDoneRatioAbove70AndCallAfter24Hours = userDoneRatioWithin24HoursMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.70 && userCalledAgainAfter24HoursMap.getOrDefault(entry.getKey(), false))
                .count();
        long usersWithDoneRatioBelow70 = userDoneRatioWithin24HoursMap.entrySet().stream()
                .filter(entry -> entry.getValue() < 0.70)
                .count();
        long usersWithDoneRatioBelow70AndCallAfter24Hours = userDoneRatioWithin24HoursMap.entrySet().stream()
                .filter(entry -> entry.getValue() < 0.70 && userCalledAgainAfter24HoursMap.getOrDefault(entry.getKey(), false))
                .count();

        // Print the results
        System.out.println("Total users (5 or more fittings in 24 hours): " + totalUsers);
        System.out.println("Users with 'done' response within 24 hours: " + usersWithDoneWithin24Hours);
        System.out.println("Users with 'done' within 24 hours and call after 24 hours: " + usersWithDoneAndCallAfter24Hours);
        System.out.println("Users without 'done' response within 24 hours: " + usersWithoutDoneWithin24Hours);
        System.out.println("Users without 'done' within 24 hours but call after 24 hours: " + usersWithoutDoneAndCallAfter24Hours);

        System.out.println("Users with 70%+ 'done' ratio within 24 hours: " + usersWithDoneRatioAbove70);
        System.out.println("Users with 70%+ 'done' ratio within 24 hours and call after 24 hours: " + usersWithDoneRatioAbove70AndCallAfter24Hours);
        System.out.println("Users with less than 70% 'done' ratio within 24 hours: " + usersWithDoneRatioBelow70);
        System.out.println("Users with less than 70% 'done' ratio within 24 hours and call after 24 hours: " + usersWithDoneRatioBelow70AndCallAfter24Hours);
    }





}
