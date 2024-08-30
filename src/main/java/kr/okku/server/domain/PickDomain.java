package kr.okku.server.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PickDomain {
    private String id;
    private String url;
    private String userId;
    private String name;
    private int price;
    private String image;
    private PlatformDomain platform;
    private String pk;
}