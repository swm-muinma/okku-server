package kr.okku.server.adapters.persistence.repository.cart;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "carts")
public class CartEntity {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private String name;

    @Field("pick_num")
    private int pickNum;

    @Field("pick_item_ids")
    private String[] pickItemIds;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("order_index") // Add order field
    private int orderIndex;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
