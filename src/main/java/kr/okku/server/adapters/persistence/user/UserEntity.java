package kr.okku.server.adapters.persistence.user;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String name;
    private String image;
    private String height;
    private String weight;
    private String form;
    @Field("is_premium")
    private Boolean isPremium;
    @Field("kakao_id")
    private String kakaoId;
    @Field("apple_id")
    private String appleId;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
