package kr.okku.server.adapters.persistence.repository.pick;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "picks")
public class PickEntity {

    @Id
    private String id;

    private String url;

    @Field("user_id")
    private String userId;

    private String name;

    private int price;

    private String image;

    private PlatformEntity platform;

    private String pk;

    @Field("fitting_images")
    private List<String> fittingImages;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public List<String> getFittingImages() {
        return this.fittingImages == null ? Collections.emptyList() : this.fittingImages;
    }
}
