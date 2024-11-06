package kr.okku.server.dto.adapter.openai;

public class TextMessageContent extends MessageContent {
    private String text;

    public TextMessageContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
