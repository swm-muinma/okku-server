package kr.okku.server.domain;

import lombok.Data;

@Data
public class ScrapedDataDomain {
    private String name;
    private int price;
    private String thumbnail_image;
    private String taskId;
    private String productPk;
    private String platform;
    private String[] taskIds;
}