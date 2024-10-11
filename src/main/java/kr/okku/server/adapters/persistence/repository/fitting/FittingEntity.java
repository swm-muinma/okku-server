package kr.okku.server.adapters.persistence.repository.fitting;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "results")
public class FittingEntity {

    @Id
    private String id;

    @Field("clothes_pk")
    private String clothesPk;

    @Field("clothes_platform")
    private String clothesPlatform;

    @Field("user_pk")
    private String userPk;

    private String status;

    @Field("img_url")
    private String imgUrl;


    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
