package kr.okku.server.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum RoleEnum {
    USER("USER"),
    ADMIN("ADMIN");
    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

}
