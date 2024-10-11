package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
public class FittingDomain {

    @Id
    private String id;

    private String clothesPk;

    private String clothesPlatform;

    private String userPk;

    private String status;

    private String imgUrl;
}
