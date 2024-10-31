package kr.okku.server.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum FormEnum {
    SLIM("slim"),
    NORMAL("normal"),
    PLUMP("plump"),
    Athletic("Athletic"),
    FAT("fat");

    private final String value;

    FormEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FormEnum fromValue(String value) {
        for (FormEnum formEnum : FormEnum.values()) {
            if (formEnum.getValue().equalsIgnoreCase(value)) {
                return formEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
