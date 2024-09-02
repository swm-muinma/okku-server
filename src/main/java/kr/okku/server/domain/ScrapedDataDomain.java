package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScrapedDataDomain {
    private String name;
    private int price;
    private String image;
    private String url;
    private String productPk;
    private String platform;
}