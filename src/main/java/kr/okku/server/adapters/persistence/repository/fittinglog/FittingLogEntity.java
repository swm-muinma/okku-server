package kr.okku.server.adapters.persistence.repository.fittinglog;

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
public class FittingLogEntity {

    @Id
    private String id;

    @Field("user_id")
    private String userId;
    @Field("user_name")
    private String userName;
    @Field("request_user_image")
    private String requestUserImage;
    @Field("request_item_image")
    private String requestItemImage;
    @Field("request_item_url")
    private String requestItemUrl;
    @Field("response_image")
    private String responseImage;
    @Field("response_message")
    private String responseMessage;
    @Field("fitting_result_id")
    private String fittingResultId;
    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
