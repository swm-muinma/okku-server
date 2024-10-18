package kr.okku.server.enums;

import lombok.Getter;

@Getter
public enum FittingStatusEnum {
    done("done"),
    processing("processing"),
    waiting("waiting"),
    serverError("serverError"),
    inputError("inputError");

    private final String value;

    FittingStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FittingStatusEnum fromValue(String value) {
        for (FittingStatusEnum formEnum : FittingStatusEnum.values()) {
            if (formEnum.getValue().equalsIgnoreCase(value)) {
                return formEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
