package kr.okku.server.adapters.persistence.repository.user;

import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewSummaryEntity;
import kr.okku.server.enums.FormEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String name;
    private String image;
    private Integer height;
    private Integer weight;
    private FormEnum form;
    @Field("is_premium")
    private Boolean isPremium;
    @Field("kakao_id")
    private String kakaoId;
    @Field("apple_id")
    private String appleId;
    @Field("fcm_token")
    private String[] fcmToken;
    @Field("user_images")
    private List<String> userImages;
    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
