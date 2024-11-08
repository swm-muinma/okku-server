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
            List<MessageContent> contents = List.of(
                    new TextMessageContent("What’s in this image?"),
                    new ImageUrlMessageContent("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
            );
            OpenAiRequestDto.Message message = new OpenAiRequestDto.Message("user", contents);
            OpenAiRequestDto requestDto = new OpenAiRequestDto("gpt-4o-mini", List.of(message));

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestDto);
            System.out.println(json);

            logger.debug("Sending request to OpenAI API: {}", requestDto);
            OpenAiResponseDto response = openaiClientAdapter.getChatCompletion("Bearer " + openAiApiKey, requestDto);
            logger.debug("Received response from OpenAI API: {}", response);

            String haiku = response.getChoices().get(0).getMessage().getContent();
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
