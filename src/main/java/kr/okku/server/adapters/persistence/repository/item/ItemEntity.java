package kr.okku.server.adapters.persistence.repository.item;

import kr.okku.server.adapters.persistence.repository.pick.PlatformEntity;
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

    private String url;

    private String name;

    private int price;

    private String image;

    private String platform;

    private String pk;

    private String brand;

    private String category;

    @Field("pick_num")
    private Integer pickNum;

    @Field("fitting_part")
    private String fittingPart;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
