package kr.okku.server.adapters.openai;

import kr.okku.server.config.FeignClientConfig;
import kr.okku.server.dto.adapter.*;
import kr.okku.server.dto.adapter.openai.OpenAiRequestDto;
import kr.okku.server.dto.adapter.openai.OpenAiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "openAiClient", url = "https://api.openai.com/v1")
public interface OpenaiClientAdapter {
    @PostMapping(value = "/chat/completions", consumes = MediaType.APPLICATION_JSON_VALUE)
    OpenAiResponseDto getChatCompletion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                        @RequestBody OpenAiRequestDto requestDto);
}