package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDomain {
    private String id;
    private String name;
    private String image;
    private String height;
    private String weight;
    private String form;
    private Boolean isPremium;
    private String kakaoId;
    private String appleId;
}




