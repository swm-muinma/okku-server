package kr.okku.server.adapters.persistence.repository.item;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "items")
public class ItemEntity {

    @Id
    private String id;

    private String platform;
    private String product_key;
    private String url;
    private String name;
    private Integer price;

    @Field("img_url")
    private String imgUrl;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
