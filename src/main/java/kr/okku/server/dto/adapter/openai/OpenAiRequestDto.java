package kr.okku.server.dto.adapter.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

public class OpenAiRequestDto {
    private String model;
    private List<Message> messages;

    @JsonProperty("response_format")
    private Map<String, Object> responseFormat;

    public OpenAiRequestDto(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;

        this.responseFormat = Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                        "name", "judgement",
                        "schema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "judgement_number", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "integer")
                                        ),
                                        "judgement_reason", Map.of("type", "string")
                                )
                        )
                )
        );
    }
    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String, Object> getResponseFormat() { // response_format의 getter
        return responseFormat;
    }

    public static class Message {
        private String role;
        private List<MessageContent> content;

        public Message(String role, List<MessageContent> content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public List<MessageContent> getContent() {
            return content;
        }
    }
}
