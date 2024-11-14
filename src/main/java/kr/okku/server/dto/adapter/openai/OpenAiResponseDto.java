package kr.okku.server.dto.adapter.openai;

import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenAiResponseDto {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    @Data
    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }

        @Data
        public static class Message {
            // content를 String으로 설정
            private String content;

            // JSON 문자열을 Content 객체로 변환
            public Content getParsedContent() {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(content, Content.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Data
            public static class Content {
                private List<Integer> judgement_number;
                private String judgement_reason;
            }
        }
    }
}
