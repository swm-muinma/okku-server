package kr.okku.server.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum FormEnum {
    SLIM("slim"),
    NORMAL("normal"),
    PLUMP("plump"), //통통
    FAT ("fat");
    private final String value;

    FormEnum(String value) {
        this.value = value;
    }

}
