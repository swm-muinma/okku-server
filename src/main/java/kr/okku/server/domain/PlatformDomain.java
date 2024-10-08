package kr.okku.server.domain;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformDomain {
    private String name;
    private String image;
    private String url;
}