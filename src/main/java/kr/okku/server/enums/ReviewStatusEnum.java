package kr.okku.server.enums;

import lombok.Getter;

@Getter
public enum ReviewStatusEnum {
    REVIEW_NOT_EXIST("REVIEW_NOT_EXIST"),
    DONE("DONE"),
    PROCESSING("PROCESSING"),
    ERROR("ERROR");

    private final String value;

    ReviewStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReviewStatusEnum fromValue(String value) {
        for (ReviewStatusEnum formEnum : ReviewStatusEnum.values()) {
            if (formEnum.getValue().equalsIgnoreCase(value)) {
                return formEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
