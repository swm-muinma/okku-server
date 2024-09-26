package kr.okku.server.domain;

import kr.okku.server.enums.FormEnum;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserDomain {
    private String id;

    @Builder.Default
    private String name = "아기 오리";

    @Builder.Default
    private String image = "";

    @Builder.Default
    private Integer height = 160;  // 기본값을 170으로 설정

    @Builder.Default
    private Integer weight = 50;   // 기본값을 65로 설정

    @Builder.Default
    private FormEnum form = FormEnum.NORMAL;  // 기본값을 FormEnum의 특정 값으로 설정

    @Builder.Default
    private Boolean isPremium = false;

    @Builder.Default
    private List<String> fcmToken= new ArrayList<>();

    private String kakaoId;
    private String appleId;

    public void addFcmToken(String token) {
        if (!fcmToken.contains(token)) {
            this.fcmToken.add(token);
        }
    }
}



