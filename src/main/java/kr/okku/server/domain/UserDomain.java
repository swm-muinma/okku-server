package kr.okku.server.domain;

import kr.okku.server.enums.FormEnum;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Stream;

@Data
@Builder
public class UserDomain {
    private String id;

    @Builder.Default
    private String name = "";

    @Builder.Default
    private String image = "";

    @Builder.Default
    private Integer height = 160;

    @Builder.Default
    private Integer weight = 50;

    @Builder.Default
    private FormEnum form = FormEnum.NORMAL;

    @Builder.Default
    private Boolean isPremium = false;

    private List<String> userImages;

    @Builder.Default
    private Set<String> fcmToken = new HashSet<String>();

    private String singleFcmToken;

    private String role;

    private String kakaoId;
    private String appleId;

    public void addFcmToken(String token) {
        fcmToken.add(token);
    }

    public String[] getFcmTokensForList(){
        List<String> temp = new ArrayList<>(fcmToken);
        return Optional.ofNullable(temp)
                .orElse(Collections.emptyList())
                .toArray(new String[0]);
    }

    public List<String> getFcmTokensForArray(){
        List<String> temp = new ArrayList<>(fcmToken);
        return List.of(Optional.ofNullable(temp)
                .orElse(Collections.emptyList())
                .toArray(new String[0]));
    }

    public void addUserImage(String image){

        List<String> modifiableUserImages = new ArrayList<>(this.userImages);
        modifiableUserImages.add(image);
        this.userImages = modifiableUserImages;
    }

    public void deleteUserImage(String url){
        if (userImages != null && userImages.contains(url)) {
            userImages.remove(url);
        }
    }
}
