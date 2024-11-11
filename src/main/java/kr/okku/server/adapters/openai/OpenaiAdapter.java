package kr.okku.server.adapters.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import kr.okku.server.domain.Log.*;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.adapter.*;
import kr.okku.server.dto.adapter.openai.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpenaiAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final OpenaiClientAdapter openaiClientAdapter;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Autowired
    public OpenaiAdapter(OpenaiClientAdapter openaiClientAdapter) {
        this.openaiClientAdapter = openaiClientAdapter;
    }
    private static final Logger logger = LoggerFactory.getLogger(OpenaiAdapter.class);

    public Optional<String> generateHaiku(String imageUrl) {

        TraceId traceId = new TraceId();
        try {
            List<MessageContent> system = List.of(
                    new TextMessageContent("입력된 사람 이미지가 당사의 정책에 위반되는지를 판단하는 전문 검수자입니다.\n" +
                            "정책에 해당하는 번호를 선택하고, 선택한 이유와 함께 반환합니다.\n" +
                            "\n" +
                            "정책 목록\n" +
                            "1. 사람 사진이 아닙니다.\n" +
                            "2. 이미지의 해상도가 너무 높습니다.\n" +
                            "3. 인위적으로 합성된 이모티콘이나 문자가 있습니다.\n" +
                            "4. 사람의 뒷모습입니다.\n" +
                            "5. 사람이 한 명이 아니라 여러명 있습니다.\n" +
                            "6. 상의나 하의 등 옷을 입힐 부분이 명확히 보이지 않습니다.\n" +
                            "7. 팔이 상의나 하의를 가리고 있어 상의나 하의를 명확히 인식할 수 없습니다.\n" +
                            "8. SNS에서 캡쳐한 사진입니다.")
            );
            List<MessageContent> contents = List.of(
                    new TextMessageContent("#요구사항: 입력되는 이미지를 분석하고, 해당하는 모든 정책을 선택하여 결과 양식에 맞게 Json으로 반환하세요. 사유는 한 문장이어야 합니다.\n" +
                            "#정책 목록:\n" +
                            "1. 사람 사진이 아닙니다.\n" +
                            "2. 이미지의 해상도가 너무 높습니다.\n" +
                            "3. 인위적으로 합성된 이모티콘이나 문자가 있습니다.\n" +
                            "4. 사람의 뒷모습입니다.\n" +
                            "5. 사람이 한 명이 아니라 여러명 있습니다.\n" +
                            "6. 상의나 하의 등 옷을 입힐 부분이 명확히 보이지 않습니다.\n" +
                            "7. 팔이 상의나 하의를 가리고 있어 상의나 하의를 명확히 인식할 수 없습니다.\n" +
                            "8. SNS에서 캡쳐한 사진입니다. \n" +
                            "#결과 양식(Json):\n" +
                            "{\n" +
                            "  \"judgement_number\":[3,8],\n" +
                            "  \"judgement_reason:\"인위적으로 합성된 이모티콘이 보이고, SNS에서 캡쳐한 사진입니다.\"\n" +
                            "}"),
                    new ImageUrlMessageContent(imageUrl)
            );
            OpenAiRequestDto.Message systemMessage = new OpenAiRequestDto.Message("system", system);
            OpenAiRequestDto.Message message = new OpenAiRequestDto.Message("user", contents);
            OpenAiRequestDto requestDto = new OpenAiRequestDto("gpt-4o-mini", List.of(systemMessage,message));

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestDto);

            logger.debug("Sending request to OpenAI API: {}", requestDto);
            OpenAiResponseDto response = openaiClientAdapter.getChatCompletion("Bearer " + openAiApiKey, requestDto);
            logger.debug("Received response from OpenAI API: {}", response);

            String haiku = response.getChoices().get(0).getMessage().getContent();
            System.out.println(response.getChoices());

            System.out.println(response.getChoices().get(0));

            System.out.println(response.getChoices().get(0).getMessage());

            System.out.println(haiku);
            log.info("{}", new ScraperReponseLogEntity(traceId, "OpenAI 요청 종료").toJson());
            return Optional.ofNullable(haiku);

        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("traceId", traceId.getId());
                scope.setExtra("imageUrl", imageUrl);
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return Optional.empty();
        }
    }
}
