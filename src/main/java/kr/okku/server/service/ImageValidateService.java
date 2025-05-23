package kr.okku.server.service;
import kr.okku.server.adapters.openai.OpenaiAdapter;
import kr.okku.server.dto.adapter.openai.OpenAiResponseDto;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ImageValidateService {

    private final OpenaiAdapter openaiAdapter;

    public ImageValidateService(OpenaiAdapter openaiAdapter) {
        this.openaiAdapter = openaiAdapter;
    }

    public String validateTest(String imageUrl) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // 병렬 작업을 위한 스레드 풀 생성

        try {
            // CompletableFuture를 사용해 병렬 작업 실행
            List<CompletableFuture<OpenAiResponseDto.Choice.Message.Content>> futures = Arrays.asList(
                    CompletableFuture.supplyAsync(() -> openaiAdapter.generateHaiku(imageUrl).orElse(null), executor),
                    CompletableFuture.supplyAsync(() -> openaiAdapter.generateHaiku(imageUrl).orElse(null), executor),
                    CompletableFuture.supplyAsync(() -> openaiAdapter.generateHaiku(imageUrl).orElse(null), executor),
                    CompletableFuture.supplyAsync(() -> openaiAdapter.generateHaiku(imageUrl).orElse(null), executor),
                    CompletableFuture.supplyAsync(() -> openaiAdapter.generateHaiku(imageUrl).orElse(null), executor)
            );

            // 모든 작업 완료 후 결과 수집
            List<OpenAiResponseDto.Choice.Message.Content> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull) // Null 결과 제거
                    .collect(Collectors.toList());

            if (results.isEmpty()) {
                // 모든 작업이 실패하거나 null일 경우 빈 문자열 반환
                return "";
            }

            // judgement_number 간의 중복값 계산
            Map<Integer, Long> frequencyMap = results.stream()
                    .flatMap(content -> content.getJudgement_number().stream())
                    .collect(Collectors.groupingBy(num -> num, Collectors.counting()));

            // 네 번 이상 등장한 값을 추출
            Set<Integer> duplicatedNumbers = frequencyMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 4)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());


            // 중복된 값을 포함한 Content 인스턴스의 judgement_reason 반환
            return results.stream()
                    .filter(content -> content.getJudgement_number().stream().anyMatch(duplicatedNumbers::contains))
                    .map(OpenAiResponseDto.Choice.Message.Content::getJudgement_reason)
                    .findFirst()
                    .orElse("");

        } finally {
            executor.shutdown(); // 스레드 풀 종료
        }
    }
}
