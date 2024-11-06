package kr.okku.server.dto.adapter.openai;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMessageContent.class, name = "text"),
        @JsonSubTypes.Type(value = ImageUrlMessageContent.class, name = "image_url")
})
public abstract class MessageContent {
    // 공통 필드가 있을 경우 여기에 추가
}

