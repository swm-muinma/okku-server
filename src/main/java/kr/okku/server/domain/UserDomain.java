package kr.okku.server.domain;

import kr.okku.server.enums.FormEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDomain {
    private String id;
    private String name;
    private String image;
    private Integer height;
    private Integer weight;
    private FormEnum form;
    private Boolean isPremium;
    private String kakaoId;
    private String appleId;
}




