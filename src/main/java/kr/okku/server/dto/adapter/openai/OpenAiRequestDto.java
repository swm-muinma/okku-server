package kr.okku.server.dto.adapter.openai;

import java.util.List;

public class OpenAiRequestDto {
    private String model;
    private List<Message> messages;

    public OpenAiRequestDto(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
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
