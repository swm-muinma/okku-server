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
    private Integer height = 160;  // 기본값을 160으로 설정

    @Builder.Default
    private Integer weight = 50;   // 기본값을 50으로 설정

    @Builder.Default
    private FormEnum form = FormEnum.NORMAL;  // 기본값을 FormEnum의 특정 값으로 설정

    @Builder.Default
    private Boolean isPremium = false;

    @Builder.Default
    private List<String> fcmToken = new ArrayList<>(); // 기본값을 빈 리스트로 설정

    private String kakaoId;
    private String appleId;

    public void addFcmToken(String token) {
        // fcmToken이 null인 경우 새 리스트를 생성
        if (fcmToken == null) {
            fcmToken = new ArrayList<>();
        }

        // 중복된 토큰 추가 방지
        if (!fcmToken.contains(token)) {
            this.fcmToken.add(token);
        }
    }
}
