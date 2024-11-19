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

    public Optional<OpenAiResponseDto.Choice.Message.Content> generateHaiku(String imageUrl) {

        TraceId traceId = new TraceId();
        try {
            List<MessageContent> system = List.of(
                    new TextMessageContent("입력된 사람 이미지가 당사의 정책에 위반되는지를 판단하는 전문 검수자입니다.\n" +
                            "확실하게 정책에 해당하는 번호만 선택하고, 선택한 이유와 함께 반환합니다.\n" +
                            "애매한 정책은 해당하지 않는 것으로 취급합니다. 확실하게 해당하는 정책만 선택합니다.\n" +
                            "해당하는 정책이 없다면 이유와 번호를 반환하지 않습니다.\n" +
                            "\n" +
                            "정책 목록\n" +
                            "1. 인위적으로 합성된 이모티콘이나 문자가 사람의 상체를 가리고 있습니다.\n" +
                            "2. 인위적으로 합성된 이모티콘이나 문자가 사람의 하체를 가리고 있습니다.\n" +
                            "3. 사람의 앞모습이 보이지 않습니다.\n" +
                            "4. 100% 서로 다른 사람이라고 판단할 수 있는 사람이 2명 이상 있습니다.\n" +
                            "5. 팔, 다리가 상의를 70%이상 가리고 있어 상의를 명확히 인식할 수 없습니다.\n"+
                            "6. 팔, 다리가 하의를 50%이상 가리고 있어 하의를 명확히 인식할 수 없습니다.\n"+
                            "7. 가방, 목걸이, 커피, 휴대폰이 상의를 70%이상 가리고 있어 상의를 명확히 인식할 수 없습니다.\n"+
                            "8. 가방, 목걸이, 커피, 휴대폰이 하의를 50%이상 가리고 있어 하의를 명확히 인식할 수 없습니다.\n"+
                            "9. 사람의 상체와 하체가 전혀 보이지 않습니다.\n")
            );
            List<MessageContent> contents = List.of(
                    new TextMessageContent("#요구사항: 입력되는 이미지를 분석하고, 확실하게 해당하는 모든 정책을 선택하여 결과 양식에 맞게 Json으로 반환하세요. 사유는 한글로 작성된 간결한 한 문장이어야 합니다. 사유에서 '40% 등의 수치'는 드러내지 않습니다. 사유에서 와 '휴대폰, 가방 등의 구체적인 사물명'은 '소지품'으로 치환합니다. '4'에 해당하는 경우 사유는 '사람이 여러명입니다'로 출력합니다.\n" +
                            "#결과 양식(Json):\n" +
                            "{\n" +
                            "  \"judgement_number\":[number],\n" +
                            "  \"judgement_reason:\"string\"\n" +
                            "}"),
                    new ImageUrlMessageContent(imageUrl)
            );
            OpenAiRequestDto.Message systemMessage = new OpenAiRequestDto.Message("system", system);
            OpenAiRequestDto.Message message = new OpenAiRequestDto.Message("user", contents);
            OpenAiRequestDto requestDto = new OpenAiRequestDto("gpt-4o", List.of(systemMessage,message));

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestDto);
            System.out.println(json);

            logger.debug("Sending request to OpenAI API: {}", requestDto);
            OpenAiResponseDto response = openaiClientAdapter.getChatCompletion("Bearer " + openAiApiKey, requestDto);
            System.out.println(response);
            logger.debug("Received response from OpenAI API: {}", response);
            System.out.println(response.getChoices().get(0).getMessage());

            OpenAiResponseDto.Choice.Message.Content haiku = response.getChoices().get(0).getMessage().getParsedContent();
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
